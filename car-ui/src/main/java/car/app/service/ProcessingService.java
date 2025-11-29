package car.app.service;

import car.app.CarAppConfiguration;
import car.common.CarProcessingConfig;
import car.engine.processor.Dataset;
import car.engine.processor.FileStatusManager;
import car.engine.processor.LocalFileStatusManager;
import car.engine.processor.Status;
import car.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ProcessingService {

    @Autowired
    CarAppConfiguration conf;

    CarProcessingConfig procConfig = new CarProcessingConfig();

    static ExecutorService exec;

    public ProcessingService() throws Exception {
        exec = Executors.newFixedThreadPool(procConfig.getThreadPoolSize());
    }

    public void ingest(Dataset dset, Set<String> filter, boolean async) throws Exception {
        doWithMultiThreads(dset, filter, false, true, true);
    }

    public void detect(Dataset dset, Set<String> filter) throws Exception {
        doWithMultiThreads(dset, filter, true, false, false);
    }

    public void index(Dataset dset, Set<String> filter) throws Exception {
        doWithMultiThreads(dset, filter, false, true, false);
    }

    void doWithMultiThreads(Dataset dset, Set<String> filters, boolean detect, boolean index, boolean ingest) throws Exception {
        String user = SecurityUtil.getCurrentUser();
        FileStatusManager fsm = new LocalFileStatusManager(dset);
        for (String file : dset.getFileStatus().keySet()) {
            if (filters != null && !filters.isEmpty() && !filters.contains(file))
                continue;
            fsm.setJobUrl(file, "");
            fsm.setStatus(file, Status.QUEUED);
            exec.submit(() -> {
                SecurityUtil.setCurrentUser(user);
                try {
                    conf.getProcessingEngine().process(dset, file, detect, index, ingest);
                } catch (Exception e) {
                    log.error("Failed to execute task", e);
                }
            });
        }
    }
}
