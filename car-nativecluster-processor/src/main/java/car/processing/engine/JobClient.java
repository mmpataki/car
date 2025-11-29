package car.processing.engine;

import car.engine.processor.FileStatusManager;
import car.engine.processor.RemoteFileStatusManager;
import car.engine.processor.Status;
import car.fs.FileManager;
import car.fs.FileSystem;
import com.mmp.sdfs.client.SdfsClient;
import com.mmp.sdfs.common.Job;
import com.mmp.sdfs.common.JobState;
import com.mmp.sdfs.common.TaskDef;
import com.mmp.sdfs.common.TaskState;
import com.mmp.sdfs.conf.SdfsClientConfig;
import com.mmp.sdfs.utils.Pair;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JobClient {

    public static void main(String args[]) throws Exception {
        new JobClient().run(args[0]);
    }

    SdfsClient sdfsClient;
    SdfsClientConfig sdfsConf;
    JobConf conf;

    boolean __debug = Boolean.getBoolean("DEBUG");

    void run(String baseDir) throws Exception {

        FileManager fm = FileManager.getInstance();

        String confFile = String.format("%s/config.properties", baseDir);
        conf = new JobConf(new String[]{"--props", confFile});

        sdfsConf = new SdfsClientConfig(new String[]{"--props", confFile});
        sdfsClient = new SdfsClient(sdfsConf);

        final String inputFilePath = conf.getFilePath();

        FileStatusManager fsm = new RemoteFileStatusManager(conf.getCarUrl(), conf.getDatasetId());
        fsm.setStatus(inputFilePath, Status.EXTRACTING);

        Job job = new Job();
        job.setJobLabel(String.format("ingest-%s/%s", conf.getDatasetId(), inputFilePath));
        job.setArtifacts(Arrays.asList(
                new Pair<>(confFile, "config.properties"),
                new Pair<>("rd.zip", "rules.zip"),
                new Pair<>("regexTokens.json", "regexTokens.json"),
                new Pair<>(__debug ? "./car-nativecluster-processor/target/car-nativecluster-processor-1.0-SNAPSHOT.jar" : "job.jar", "job.jar")
        ));
        List<TaskDef> tasks = new ArrayList<>();
        job.setTasks(tasks);


        List<FileSystem.Partition> partitions = fm.getPartitions(conf.getDatasetId(), inputFilePath, conf.getNumExecutors());
        partitions.forEach(p -> {
            TaskDef task = TaskDef.builder()
                    .memNeeded(512 * 1024 * 1024)
                    .cpuPercentNeeded(10)
                    .taskLabel("chunk-" + p.getStart())
                    .command(Arrays.asList("java", "-jar", "job.jar", inputFilePath, String.valueOf(p.getStart())))
                    .preferredNodes(p.getHosts())
                    .build();
            tasks.add(task);
        });

        float completedTasks = 0;

        sdfsClient.submit(job, new SdfsClient.JobUpdateCallBack() {

            boolean urlUpdated = false;

            @SneakyThrows
            @Override
            public void jobUpdated(JobState js) {
                log.info("Job state updated {} ({}) : {}", js.getJobId(), js.getJobLabel(), js.getState());
                if (js.getState().isCompleted()) {
                    try {
                        sdfsClient.delete(inputFilePath);
                    } catch (Exception e) {
                        log.error("Error while deleting input file : {}, delete it manually", inputFilePath, e);
                    }
                    fsm.setStatus(inputFilePath, js.getState() == JobState.State.SUCCEEDED ? Status.EXTRACTED : Status.FAILED);
                }
                if (js.getState().hasRun() && !urlUpdated) {
                    fsm.setJobUrl(inputFilePath, String.format("http://%s:%d/jobs.html?q=%s", sdfsConf.getNnHost(), sdfsConf.getInfoPort(), js.getJobId()));
                    urlUpdated = true;
                }
            }

            @SneakyThrows
            @Override
            public void taskUpdated(TaskState taskState) {
                log.info("Task state updated {} ({}) {}", taskState.getTaskId(), taskState.getTaskLabel(), taskState.getState());
                if (taskState.getState().isCompleted()) {
                    if (taskState.getExitCode() == 0)
                        fsm.setFileProcessPercent(inputFilePath, (completedTasks + 1.0f) / partitions.size());
                }
            }
        });
    }
}
