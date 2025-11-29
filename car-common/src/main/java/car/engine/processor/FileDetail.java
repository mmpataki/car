package car.engine.processor;

import car.engine.processor.filelocations.FileLocation;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDetail {

    String logTypeGroup;
    String logType;

    long size, uploadTime;
    Status state;

    float processPercent;
    String jobUrl;

    String mappedFileName;
    FileLocation location;

    public void reset() {
        logType = logTypeGroup = null;
        processPercent = 0;
        mappedFileName = null;
        state = Status.NOTDONE;
        jobUrl = null;
    }
}
