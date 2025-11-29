package car.repos;

import car.engine.processor.Dataset;
import car.engine.processor.FileDetail;
import car.engine.processor.Status;
import car.engine.processor.filelocations.LocalLocation;
import car.engine.rules.FileBasedRuleManager;
import car.fs.FileManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
public class DatasetManager {

    FileManager fm;
    DatasetRepo repo;

    public DatasetManager(DatasetRepo repo, FileManager fm) {
        this.repo = repo;
        this.fm = fm;

        try {
            Dataset.setRM(new FileBasedRuleManager());
            Dataset.setDR(repo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Dataset getDataSet(String dsetId) throws Exception {
        return discoverFiles(repo.findById(dsetId));
    }

    private Dataset discoverFiles(Dataset dset) {
        try {
            fm.listFiles(dset.getId()).forEach(fs -> {
                dset.getFileStatus().computeIfAbsent(
                        fs.getPath(),
                        fpath -> FileDetail.builder().state(Status.NEW).size(fs.getLength()).location(LocalLocation.makeLocalLocation(fpath)).build()
                );
            });
        } catch (Exception e) {
            log.error("Error while dicovering files for {}", dset.getId());
        }
        return dset;
    }

    public void save(Dataset dset) throws Exception {
        repo.save(dset);
    }

    public List<Dataset> search(String q, int offset, int pageSize) throws Exception {
        List<Dataset> list = repo.findByIdOrDescription(q);
        if (list.size() <= offset)
            return Collections.emptyList();
        List<Dataset> datasets = list.subList(offset, Math.min(list.size(), offset + pageSize));
        datasets.forEach(this::discoverFiles);
        return datasets;
    }

}
