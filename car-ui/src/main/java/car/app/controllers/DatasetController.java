package car.app.controllers;

import car.app.service.DatasetService;
import car.app.service.RuleService;
import car.app.service.UserConfigService;
import car.engine.models.SearchQuery;
import car.engine.models.SearchResponse;
import car.engine.processor.Dataset;
import car.engine.processor.FileDetail;
import car.engine.store.StoreResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/datasets")
@CrossOrigin("*")
public class DatasetController {

    @Autowired
    DatasetService DS;

    @Autowired
    UserConfigService US;

    @GetMapping("")
    public List<DatasetService.DatasetSummary> searchDataSets(
            @RequestParam("q") String q,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int pageSize) throws Exception {
        return DS.searchDatasets(q, offset, pageSize);
    }

    @GetMapping("{id}")
    public Dataset getDataSet(@PathVariable String id) throws Exception {
        return DS.getDataset(id);
    }

    @GetMapping("{id}/schema/{storetype}")
    public Object getDataSetSchema(@PathVariable String id, @PathVariable String storetype) throws Exception {
        return DS.getDatasetSchema(id, storetype);
    }

    @PostMapping
    public Dataset postDataset(@RequestBody Dataset dset) throws Exception {
        return DS.createDataset(dset);
    }

    @PostMapping("{id}/files")
    public String handleFileUpload(@PathVariable String id, @RequestParam("file") MultipartFile file, String logTypeGroup, String logType) throws Exception {
        return DS.addFile(id, file, logTypeGroup, logType);
    }

    @PostMapping("{id}/search")
    public SearchResponse getLogMessages(@PathVariable String id, @RequestBody SearchQuery q) throws Exception {
        return DS.search(id, q);
    }

    @PostMapping("{id}/analyze")
    public void analyze(@PathVariable String id) throws Exception {
        DS.ingest(id, Collections.emptySet(), true);
    }

    // do not accept the user name here
    @DeleteMapping("{id}/cleanup")
    public void delete(@PathVariable String id) throws Exception {
        DS.cleanup(id);
    }

    @GetMapping("{id}/status")
    public Map<String, FileDetail> status(@PathVariable String id) throws Exception {
        return DS.status(id);
    }

    @GetMapping("{id}/indexfields")
    public Set<String> getIndexFields(@PathVariable String id) throws Exception {
        return DS.getIndexFields(id);
    }

    @PostMapping("{id}/query/sql")
    public StoreResponse query(@PathVariable String id, @RequestParam String sql) throws Exception {
        return DS.query(id, sql.replace("\\", "\\\\"));
    }

    @PostMapping("{id}/analyzefile")
    public void analyzeFile(@PathVariable String id, @RequestParam String file) throws Exception {
        DS.ingest(id, Collections.singleton(file), true);
    }

    @GetMapping("{id}/updateFileState")
    public void updateFileStatus(@PathVariable String id, @RequestParam String file,
                                 @RequestParam(required = false) String logTypeGrp, @RequestParam(required = false) String logType,
                                 @RequestParam(required = false) String state,
                                 @RequestParam(required = false) String processPercent,
                                 @RequestParam(required = false) String jobUrl
    ) throws Exception {
        DS.updateFileStatus(id, file, logTypeGrp, logType, state, processPercent, jobUrl);
    }

    @RequestMapping("{id}/downloadfile")
    public void getFile(@PathVariable String id, @RequestParam("file") List<String> files, HttpServletResponse response) throws Exception {

        File file = DS.downloadFiles(id, files);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + file.getName());

        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(file);
        IOUtils.copy(in, out);

        out.close();
        in.close();
        file.delete();
    }
}
