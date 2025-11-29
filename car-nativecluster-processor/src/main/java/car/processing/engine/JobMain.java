package car.processing.engine;

import car.app.service.RegexService;
import car.common.CarProcessingConfig;
import car.engine.models.SearchQuery;
import car.engine.models.SearchResponse;
import car.engine.processor.CarEngine;
import car.engine.processor.EngineOptions;
import car.engine.processor.TrackingInputStream;
import car.engine.query.Query;
import car.engine.recordreader.RecordReader;
import car.engine.rules.FileBasedRuleManager;
import car.engine.rules.LogType;
import car.engine.store.*;
import car.fs.FileManager;
import com.mmp.sdfs.conf.SdfsClientConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
public class JobMain {

    public static void main(String[] args) throws Exception {
        try {
            process(args);
            System.exit(0);
        } catch (Throwable e) {
            log.error("error in processing, exiting", e);
            System.exit(1);
        }
    }

    static void process(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: inputPath seekPos");
            System.exit(1);
        }

        String fullPath = args[0];
        long pos = Long.parseLong(args[1]);
        log.info("Input: {}, Starting read at : {}", fullPath, pos);

        log.info("Extracting rules.zip");
        extractZip("./rules.zip");

        log.info("Files in CWD: ");
        Files.walk(Paths.get(".")).forEach(p -> log.info(p.toAbsolutePath().toString()));

        SdfsClientConfig sdfsConf = new SdfsClientConfig(new String[]{"--props", "./config.properties"});
        JobConf conf = new JobConf();

        log.info("Job config {}", conf);
        log.info("SDFS config {}", sdfsConf);

        StoreManager SM = new StoreManager();

        String dsetName = conf.getDatasetId(), filePath = conf.getFilePath();
        Store<StoreResponse, Document, Query> store = new BatchingStore<>(SM.getStore(dsetName), 10000);
        Store<SearchResponse, Document, SearchQuery> iStore = new BatchingStore<>(SM.getIndexStore(dsetName), 10000);

        FileManager fm = FileManager.getInstance();

        CarEngine carEngine;
        try (InputStream siis = fm.open(dsetName, fullPath)) {

            TrackingInputStream iis = new TrackingInputStream(siis);

            FileBasedRuleManager rp = new FileBasedRuleManager();
            LogType logType = rp.getLogType(conf.getLogTypeGrp(), conf.getLogType());
            RecordReader rr = logType.getRecordReader();
            rr.setInputStream(iis);
            rr.setFileName(filePath);
            rr.init(RegexService.getRegexTokens());
            rr.setLimit(conf.getReadLimit());

            // seek after record reader init because we want record reader which read file header first. eg. csv
            if (pos != 0) {
                siis.skip(pos - iis.getBytesRead());
                iis.reset();
                rr.setInputStream(iis);
            }

            EngineOptions engineOpts = EngineOptions.builder()
                    .fileName(filePath)
                    .dsetName(dsetName)
                    .logTypeGroup(conf.getLogTypeGrp())
                    .logType(conf.getLogType())
                    .recordReader(rr)
                    .ruleProvider(rp)
                    .conf(new CarProcessingConfig())
                    .index(conf.isDoIndex())
                    .detect(conf.isDoDetect())
                    .ingest(conf.isDoIngest())
                    .store(store)
                    .indexStore(iStore)
                    .byteLimit(conf.getReadLimit())
                    .build();
            carEngine = new CarEngine(engineOpts);
            carEngine.process();
            log.info("Processing complete!");
        } finally {
            try {
                store.close();
            } catch (Exception ignore) {
            }
            try {
                iStore.close();
            } catch (Exception ignore) {
            }
        }
    }


    public static void extractZip(String zipFile) throws IOException {
        int buffer = 2048;
        File file = new File(zipFile);
        try (ZipFile zip = new ZipFile(file)) {
            String newPath = ".";
            Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();
            while (zipFileEntries.hasMoreElements()) {
                ZipEntry entry = zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                File destFile = new File(newPath, currentEntry);
                log.info("unzip" + destFile);
                File destinationParent = destFile.getParentFile();
                destinationParent.mkdirs();
                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    byte[] data = new byte[buffer];
                    FileOutputStream fos = new FileOutputStream(destFile);
                    try (BufferedOutputStream dest = new BufferedOutputStream(fos, buffer)) {
                        while ((currentByte = is.read(data, 0, buffer)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                        is.close();
                    }
                }
            }
        }
    }
}
