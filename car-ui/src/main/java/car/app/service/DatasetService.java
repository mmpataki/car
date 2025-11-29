package car.app.service;

import car.app.service.api.AuthorizationService;
import car.engine.models.SearchQuery;
import car.engine.models.SearchResponse;
import car.engine.processor.Dataset;
import car.engine.processor.FileDetail;
import car.engine.processor.Status;
import car.engine.query.Query;
import car.engine.recordreader.RecordReader;
import car.engine.recordreader.StructuredRecordReader;
import car.engine.rules.LogType;
import car.engine.rules.LogTypeGroup;
import car.engine.search.SearchView;
import car.engine.store.Document;
import car.engine.store.Store;
import car.engine.store.StoreManager;
import car.engine.store.StoreResponse;
import car.engine.util.Pair;
import car.repos.DatasetManager;
import car.repos.DatasetRepo;
import car.repos.FileBasedDatasetRepo;
import car.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class DatasetService {

    @Autowired
    RuleService RS;

    StoreManager SM = new StoreManager();

    @Autowired
    TempFileDeletionService TFDS;

    @Autowired
    AuthorizationService AS;

    @Autowired
    ProcessingService PS;

    @Autowired
    UserConfigService US;

    @Autowired
    FileSystemService FS;

    @Autowired
    SearchService SS;

    DatasetManager DM;

    @PostConstruct
    public void afterConstruct() {
        DatasetRepo repo = new FileBasedDatasetRepo();
        DM = new DatasetManager(repo, FS.fm);
    }

    public DatasetService() throws Exception {
    }

    public Object getDatasetSchema(String dsetId, String storetype) throws Exception {
        return storetype.equals("logs")
                ? SM.getIndexStore(dsetId).getMetadata()
                : SM.getStore(dsetId).getMetadata();
    }

    public Set<String> getIndexFields(String dsetId) throws Exception {
        Dataset ds = getDataset(dsetId);
        return ds.getTypes().stream().flatMap(typ -> RS.getLogTypes(typ).stream().flatMap(lt -> {
            RecordReader rr = lt.getRecordReader();
            if (rr instanceof StructuredRecordReader) {
                return rr.getFieldNames().stream();
            }
            return Stream.empty();
        })).collect(Collectors.toSet());
    }

    public File downloadFiles(String id, List<String> files) throws Exception {
        return FS.downloadFiles(id, files.isEmpty() ? new LinkedList<>(getDataset(id).getFileStatus().keySet()) : files);
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class DatasetSummary {
        String id, name, owner, description;
        int numFiles;
    }

    public List<DatasetSummary> searchDatasets(String q, int offset, int pageSize) throws Exception {
        return DM.search(q, offset, pageSize).stream().map(d ->
                DatasetSummary.builder().id(d.getId()).name(d.getName()).description(d.getDescription()).owner(d.getOwner()).numFiles(d.getFileStatus().size()).build()
        ).collect(Collectors.toList());
    }

    @SneakyThrows
    public DatasetSummary makeDatasetSummary(String dsId) {
        Dataset dataset = getDataset(dsId);
        return new DatasetSummary(dsId, dataset.getName(), dataset.getOwner(), dataset.getDescription(), dataset.getFileStatus().size());
    }

    public Dataset createDataset(Dataset dset) throws Exception {
        if (dset.getName() == null || dset.getName().isEmpty()) {
            throw new IllegalArgumentException("dataset name can't be empty");
        }
        String owner = SecurityUtil.getCurrentUser();
        if (dset.getId() == null) {
            dset.setId(makeKey(owner, dset.getName()));
        }
        DM.save(dset);
        return dset;
    }

    private static String makeKey(String owner, String name) {
        return String.format("%s_%s", owner, name);
    }

    public Dataset getDataset(String id) throws Exception {
        return DM.getDataSet(id);
    }

    public String addFile(String dsetId, MultipartFile file, String logTypeGroup, String logType) throws Exception {
        Dataset ds;
        String owner = SecurityUtil.getCurrentUser();
        try {
            ds = getDataset(dsetId);
        } catch (Exception e) {
            if (dsetId.endsWith("_adhoc")) {
                ds = Dataset.builder()
                        .name("adhoc")
                        .owner(owner)
                        .description("random files")
                        .types(RS.getLogTypeGroups().stream().map(LogTypeGroup::getName).collect(Collectors.toList()))
                        .build();
                createDataset(ds);
            } else {
                throw e;
            }
        }
        String fileName = file.getOriginalFilename() + "";
        if (dsetId.endsWith("_adhoc")) {
            fileName = System.currentTimeMillis() + "_" + fileName;
        }

        Map<String, Long> addedFiles = FS.addFile(dsetId, fileName, file.getInputStream());
        for (Map.Entry<String, Long> addedFile : addedFiles.entrySet()) {
            ds.addFile(addedFile.getKey(), logTypeGroup, logType, addedFile.getValue());
        }
        if (!addedFiles.isEmpty())
            ingest(ds, addedFiles.keySet(), false);
        return addedFiles.keySet().stream().findFirst().orElse("");
    }


    private SearchView suggestView(SearchResponse resp) throws Exception {
        Map<String, Map<String, Long>> facets = resp.getFacets();
        Map<String, Long> lt = facets.get("_logtyp"), lgt = facets.get("_logtypgrp");
        if (lt != null && lt.size() == 1 && lgt != null && lgt.size() == 1) {
            LogType logType = RS.getLogType(lgt.keySet().stream().findFirst().get(), lt.keySet().stream().findFirst().get());
            if (logType.getDefaultSearchView() != null)
                return SS.getSearchView(logType.getDefaultSearchView());
        }
        return null;
    }

    public SearchResponse search(String dsetId, SearchQuery sq) throws Exception {
        Store<SearchResponse, Document, SearchQuery> store = SM.getIndexStore(dsetId);
        SearchView searchView = null;
        if (sq.isSuggestView()) {
            int pageSize = sq.getPageSize();
            List<String> facetFields = sq.getFacetFields();

            sq.setPageSize(0);
            sq.setFacetFields(Arrays.asList("_logtyp", "_logtypgrp"));
            SearchResponse resp = store.search(sq);

            sq.setPageSize(pageSize);
            sq.setFacetFields(facetFields);

            searchView = suggestView(resp);
            if (searchView != null) {
                List<String> fields = searchView.getFields().stream()
                        .filter(SearchView.SearchField::isQueried)
                        .map(SearchView.SearchField::getName)
                        .collect(Collectors.toList());
                if (!fields.isEmpty())
                    sq.setFields(fields);
            }
        }
        SearchResponse resp = store.search(sq);
        if (searchView != null)
            resp.setSuggestedView(searchView.getName());
        return resp;
    }

    public void index(Dataset dset, Set<String> filter) throws Exception {
        PS.index(dset, filter);
    }

    public void detect(Dataset dset, Set<String> filter) throws Exception {
        PS.detect(dset, filter);
    }

    public void ingest(Dataset dset, Set<String> filter, boolean async) throws Exception {
        PS.ingest(dset, filter, async);
    }

    public void ingest(String dsetId, Set<String> filter, boolean async) throws Exception {
        ingest(getDataset(dsetId), filter, async);
    }

    public Map<String, FileDetail> status(String dsetId) throws Exception {
        return getDataset(dsetId).getFileStatus();
    }

    public StoreResponse query(String datasetId, String sql) throws Exception {
        return SM.getStore(datasetId).search(Query.builder().query(sql).build());
    }

    public void cleanup(String datasetId) throws Exception {
        AS.checkCleanUpPermission(datasetId);
        SM.deleteStores(datasetId);
        getDataset(datasetId).cleanup();
    }

    public void updateFileStatus(String dsetId, String file, String logTypeGrp, String logType, String state, String processPercent, String jobUrl) throws Exception {
        Dataset dataset = getDataset(dsetId);
        if (state != null)
            dataset.setStatus(file, Status.valueOf(state));
        if (logType != null && logTypeGrp != null)
            dataset.setType(file, logTypeGrp, logType);
        if (processPercent != null)
            dataset.setProcessPercent(file, Float.parseFloat(processPercent));
        if (jobUrl != null)
            dataset.setJobUrl(file, jobUrl);
        dataset.save();
    }

}
