package car.engine.processor;

import car.engine.processor.filelocations.LocalLocation;
import car.engine.rules.LogType;
import car.engine.rules.RuleManager;
import car.repos.DatasetRepo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class Dataset {


    /* a lot of code updates the dataset from outside  */
    @Setter
    transient static RuleManager RM;

    @Setter
    transient static DatasetRepo DR;

    String name, owner, id, externalUrl;
    String description;
    String timeZoneOffset;
    List<String> types;
    Map<String, Object> attributes;
    Map<String, FileDetail> fileStatus = new HashMap<>();
    transient boolean dirty;

    public void addFile(String fname, String logTypeGroup, String logType, long length) throws Exception {
        fileStatus.put(
                fname,
                FileDetail.builder()
                        .logTypeGroup(logTypeGroup)
                        .logType(logType)
                        .size(length)
                        .uploadTime(System.currentTimeMillis())
                        .state((logTypeGroup != null && logType != null) ? Status.DETECTED : Status.NEW)
                        .location(LocalLocation.makeLocalLocation(fname))
                        .build()
        );
    }

    public void cleanup() throws Exception {
        fileStatus.forEach((k, v) -> {
            v.reset();
        });
        save();
    }

    public void save() throws Exception {
        DR.save(this);
    }

    public void setType(String filePath, String logTypeGrp, String logTypeName) throws Exception {

        FileDetail fd = fileStatus.get(filePath);
        if (fd == null)
            throw new Exception("This is a bug, a file was not found but reported");

        if (logTypeGrp != null && logTypeName != null) {
            LogType logType = RM.getLogType(logTypeGrp, logTypeName);
            fd.setMappedFileName(logType.normalizeFileName(filePath));
        }

        fd.setLogTypeGroup(logTypeGrp);
        fd.setLogType(logTypeName);
        fd.setState((logTypeGrp == null || logTypeName == null) ? Status.NOT_DETECTED : Status.DETECTED);
        fileStatus.put(filePath, fd);
    }

    public void setStatus(String filePath, Status status) {
        FileDetail fileDetail = fileStatus.computeIfAbsent(filePath, fp -> FileDetail.builder().build());
        fileDetail.setState(status);
    }

    public void setProcessPercent(String filePath, float percent) {
        FileDetail fileDetail = fileStatus.computeIfAbsent(filePath, fp -> FileDetail.builder().build());
        fileDetail.setProcessPercent(percent);
    }

    public FileDetail statusOf(String fileName) {
        return fileStatus.get(fileName);
    }

    public void setJobUrl(String file, String jobUrl) {
        fileStatus.get(file).setJobUrl(jobUrl);
    }

}
