package car.engine.processor;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Slf4j
public class RemoteFileStatusManager extends FileStatusManager {

    private final String carUrl;

    public RemoteFileStatusManager(String carUrl, String dsetId) {
        super(dsetId);
        this.carUrl = carUrl;
    }

    @Override
    public void setStatus(String filePath, Status status) throws Exception {
        updateFileStatus(filePath, null, null, status.toString(), null, null);
    }

    @Override
    public void setLogType(String filePath, String logTypeGrp, String logType) throws Exception {
        updateFileStatus(filePath, logTypeGrp, logType, Status.DETECTED.toString(), null, null);
    }

    @Override
    public void setFileProcessPercent(String file, float percent) throws Exception {
        updateFileStatus(file, null, null, null, String.valueOf(percent), null);
    }

    @Override
    public void setJobUrl(String file, String jobUrl) throws Exception {
        updateFileStatus(file, null, null, null, null, jobUrl);
    }

    void updateFileStatus(String file, String logTypeGrp, String logType, String state, String processPercent, String jobUrl) throws Exception {
        String req = carUrl + String.format("/api/datasets/%s/updateFileState?file=%s", e(dsetId), e(file));
        if (logTypeGrp != null)
            req += "&logTypeGroup=" + e(logTypeGrp);
        if (logType != null)
            req += "&logTypeGroup=" + e(logType);
        if (state != null)
            req += "&state=" + e(state);
        if (processPercent != null)
            req += "&processPercent=" + e(processPercent);
        if (jobUrl != null)
            req += "&jobUrl=" + e(jobUrl);
        log.info("update req = {}", req);
        HttpURLConnection http = (HttpURLConnection) (new URL(req)).openConnection();
        http.setDoOutput(false);
        http.getInputStream().read();
    }

    String e(String s) {
        return URLEncoder.encode(s);
    }


    @Override
    public void close() throws IOException {
    }
}
