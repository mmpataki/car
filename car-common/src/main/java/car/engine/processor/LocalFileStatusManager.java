package car.engine.processor;

import lombok.SneakyThrows;

import java.io.IOException;

public class LocalFileStatusManager extends FileStatusManager {

    private final Dataset ds;

    public LocalFileStatusManager(Dataset ds) {
        super(ds.getId());
        this.ds = ds;
    }

    @Override
    public void setStatus(String filePath, Status status) throws Exception {
        ds.setStatus(filePath, status);
        ds.save();
    }

    @Override
    public void setLogType(String filePath, String logTypeGrp, String logType) throws Exception {
        ds.setType(filePath, logTypeGrp, logType);
        ds.save();
    }

    @Override
    public void setFileProcessPercent(String file, float percent) throws Exception {
        ds.statusOf(file).setProcessPercent(percent);
        ds.save();
    }

    @Override
    public void setJobUrl(String file, String jobUrl) throws Exception {
        ds.statusOf(file).setJobUrl(jobUrl);
        ds.save();
    }

    @SneakyThrows
    @Override
    public void close() throws IOException {
        ds.save();
    }
}
