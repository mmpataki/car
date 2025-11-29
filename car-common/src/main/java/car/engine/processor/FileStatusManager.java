package car.engine.processor;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Closeable;

@Data
@AllArgsConstructor
public abstract class FileStatusManager implements Closeable {

    String dsetId;

    public abstract void setStatus(String filePath, Status status) throws Exception;

    public abstract void setLogType(String filePath, String logTypeGrp, String logType) throws Exception;

    public abstract void setFileProcessPercent(String file, float percent) throws Exception;

    public abstract void setJobUrl(String file, String jobUrl) throws Exception;

    public void updateFile(String filePath, String logTypeGrp, String logType, Status status) throws Exception {
        setStatus(filePath, status);
        setLogType(filePath, logTypeGrp, logType);
    }

}
