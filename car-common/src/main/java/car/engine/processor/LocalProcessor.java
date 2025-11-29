package car.engine.processor;

import car.app.service.RegexService;
import car.common.CarProcessingConfig;
import car.engine.models.SearchQuery;
import car.engine.models.SearchResponse;
import car.engine.query.Query;
import car.engine.recordreader.RecordReader;
import car.engine.rules.FileBasedRuleManager;
import car.engine.rules.LogType;
import car.engine.store.*;
import car.engine.util.Pair;
import car.fs.FileManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.InputStream;

@Getter
@Setter
@Slf4j
public class LocalProcessor extends ProcessingEngine {

    boolean sync;

    StoreManager SM = new StoreManager();
    CarProcessingConfig conf = new CarProcessingConfig();

    public LocalProcessor() throws Exception {
    }

    @Override
    public void process(Dataset dset, String filePath, boolean detect, boolean index, boolean ingest) throws Exception {

        String dsetId = dset.getId();
        FileDetail fileDetail = dset.statusOf(filePath);

        FileManager fm = FileManager.getInstance();
        FileBasedRuleManager ruleProvider = new FileBasedRuleManager();

        try (InputStream dis = fm.open(dsetId, filePath);
             InputStream iis = fm.open(dsetId, filePath);
             FileStatusManager fsm = new LocalFileStatusManager(dset)) {
            EngineOptions opts = EngineOptions.builder()
                    .fileName(filePath)
                    .dsetName(dsetId)
                    .logTypeGroup(fileDetail != null ? fileDetail.getLogTypeGroup() : null)
                    .logType(fileDetail != null ? fileDetail.getLogType() : null)
                    .detectStream(dis)
                    .ruleProvider(ruleProvider)
                    .conf(getConf())
                    .index(index)
                    .detect(detect)
                    .ingest(ingest)
                    .possibleLogTypeGroups(dset.getTypes())
                    .byteLimit(Long.MAX_VALUE)
                    .build();
            if (detect || fileDetail == null || !fileDetail.getState().isDetected()) {
                fsm.setStatus(filePath, Status.DETECTING);
                Pair<String, String> status = new CarEngine(opts).detect();
                if (status != null) {
                    fsm.setLogType(filePath, status.getFirst(), status.getSecond());
                } else {
                    fsm.setLogType(filePath, null, null);
                    return;
                }
            }
            if (index || ingest) {
                if (!fileDetail.getState().isDetected())
                    throw new IllegalStateException("ingest / index without detection");
                fsm.setStatus(filePath, Status.EXTRACTING);
                Store<StoreResponse, Document, Query> store = new BatchingStore<>(SM.getStore(dsetId), 10000);
                Store<SearchResponse, Document, SearchQuery> iStore = new BatchingStore<>(SM.getIndexStore(dsetId), 10000);
                opts.setStore(store);
                opts.setIndexStore(iStore);

                opts.setLogTypeGroup(fileDetail.getLogTypeGroup());
                opts.setLogType(fileDetail.getLogType());

                LogType logType = ruleProvider.getLogType(fileDetail.getLogTypeGroup(), fileDetail.getLogType());
                RecordReader rr = logType.getRecordReader();
                rr.setInputStream(new TrackingInputStream(iis));
                rr.setFileName(filePath);
                rr.init(RegexService.getRegexTokens());
                rr.setLimit(opts.getByteLimit());
                opts.setRecordReader(rr);

                try {
                    new CarEngine(opts).process();
                    fsm.setFileProcessPercent(filePath, 100);
                    fsm.setStatus(filePath, Status.EXTRACTED);
                } catch (Exception e) {
                    log.error("Error while processing", e);
                    fsm.setStatus(filePath, Status.FAILED);
                } finally {
                    store.close();
                    iStore.close();
                }
            }
        }
    }
}
