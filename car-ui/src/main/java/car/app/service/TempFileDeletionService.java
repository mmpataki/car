package car.app.service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
public class TempFileDeletionService {

    /* 6 hrs */
    private static final long DELETE_INTERVAL = 1000 * 60 * 60 * 6;

    @PostConstruct
    public void init() {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Q.forEach(ff -> {
                delete(ff.getPath());
            });
        }));

        new Thread(() -> {
            while (true) {
                try {
                    FileAndTime ff = Q.take();
                    long timeLeft = ff.getTime() + (DELETE_INTERVAL) - System.currentTimeMillis();
                    if (timeLeft > 500) {
                        Thread.sleep(timeLeft);
                    }
                    delete(ff.getPath());
                } catch (Exception e) {
                    log.error("Exception in tempfiledeletion service ", e);
                }
            }
        }).start();
    }

    private void delete(String path) {
        try {
            new File(path).delete();
            log.info("Deleted " + path);
        } catch (Exception e) {
            log.error("Error while deleting " + path);
        }
    }

    @Data
    @Builder
    static class FileAndTime {
        String path;
        long time;
    }

    BlockingQueue<FileAndTime> Q = new LinkedBlockingQueue<>();

    /* TODO: fix for archives */
    public void watchFile(String file) {
        Q.add(FileAndTime.builder().time(System.currentTimeMillis()).path(file).build());
    }

}
