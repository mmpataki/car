package car.repos;

import car.engine.processor.Dataset;
import car.engine.util.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class FileBasedDatasetRepo implements DatasetRepo {

    static String basedir = "./datasets";
    static final String DATASET_DEF = ".dataset";
    static Map<String, Dataset> cache = new ConcurrentHashMap<>();


    static String getDsetDefFile(String name) {
        return String.format("%s/%s", getDsetDir(name), DATASET_DEF);
    }

    public FileBasedDatasetRepo() {
        new Thread(() -> {
            while (true) {
                cache.forEach((p, d) -> {
                    try {
                        save(d);
                    } catch (Exception e) {
                        log.error("Error while syncing dsets", e);
                    }
                });
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException ignored) {
                }
            }
        }, "dset-cache-sync").start();
    }

    static String getDsetDir(String id) {
        return String.format("%s/%s", basedir, id);
    }

    @Override
    @SneakyThrows
    public Dataset findById(String id) {
        String defFile = getDsetDefFile(id);
        if (!new File(defFile).exists()) {
            throw new FileNotFoundException("Dataset " + id + " doesn't exist. Path: " + defFile);
        }
        Dataset dset = JsonUtil.read(defFile, Dataset.class);
        dset.setId(id);
        return dset;
    }

    private String getDSPath(String id) {
        return getDsetDir(id);
    }

    @Override
    public void save(Dataset ds) throws IOException {
        Files.createDirectories(Paths.get(getDSPath(ds.getId())));
        JsonUtil.save(getDsetDefFile(ds.getId()), ds);
    }

    @Override
    public List<Dataset> findByIdOrDescription(String q) throws Exception {
        final String sq = q.toLowerCase();
        String[] files = new File(basedir).list();
        if (files != null)
            return Arrays.stream(files)
                    .filter(f -> !f.startsWith("."))
                    .map(this::findById)
                    .filter(d -> d.getId().toLowerCase().contains(sq) || (d.getDescription() != null && d.getDescription().toLowerCase().contains(sq)))
                    .collect(Collectors.toList());
        return Collections.emptyList();
    }

}
