package car.processing.engine;

import car.common.Argument;
import car.common.CarProcessingConfig;
import car.engine.processor.*;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

@Data
@Slf4j
public class SdfsClusterProcessor extends ProcessingEngine {

    static boolean __debug = true;

    @Getter
    static class DistributedProcessorConfiguration extends CarProcessingConfig {

        @Argument(keys = {"--sdfs.clusterprocessor.headnode.host"}, help = "SDFS Namenode host", required = true)
        String nameNodeHost;

        @Argument(keys = {"--sdfs.clusterprocessor.headnode.port"}, help = "SDFS Namenode port", required = true)
        int nameNodePort;

        @Argument(keys = {"--sdfs.clusterprocessor.block.size"}, help = "Block size",  required = true)
        int blockSize;

        public DistributedProcessorConfiguration(String[] args) throws Exception {
            super(args);
        }

        public DistributedProcessorConfiguration() throws Exception {
            super();
        }
    }

    DistributedProcessorConfiguration conf = new DistributedProcessorConfiguration();

    public SdfsClusterProcessor() throws Exception {
        super();
    }

    @Override
    public void process(Dataset dset, String filePath, boolean detect, boolean index, boolean ingest) throws Exception {
        String jobId = UUID.randomUUID().toString();

        //detection on cluster doesn't make sense, do it here
        FileDetail fs = dset.statusOf(filePath);
        if (detect || fs == null || fs.getState().ordinal()  <= Status.NOT_DETECTED.ordinal()) {
            LocalProcessor localOne = new LocalProcessor();
            localOne.setSync(true);
            localOne.process(dset, filePath, true, false, false);
        }
        fs = dset.statusOf(filePath);
        if (fs == null || fs.getState().ordinal() <= Status.NOT_DETECTED.ordinal()) {
            log.warn("{} is not detected, skipping analysis", filePath);
            return;
        }

        JobConf conf = new JobConf();
        conf.setJobId(jobId);
        conf.setDatasetId(dset.getId());
        conf.setFilePath(filePath);
        conf.setDoIndex(index);
        conf.setDoDetect(false);
        conf.setDoIngest(ingest);
        conf.setUserId(dset.getOwner());
        conf.setLogType(fs.getLogType());
        conf.setLogTypeGrp(fs.getLogTypeGroup());
        conf.setReadLimit(this.conf.getBlockSize());
        conf.setCarUrl(getConf().getCarUrl());

        invoke(conf);
    }

    private void invoke(JobConf conf) throws Exception {

        Properties props = new Properties();
        props.load(new FileInputStream("./config.properties"));
        props.putAll(conf.getAsMap());
        props.setProperty("nnHost", this.conf.getNameNodeHost());
        props.setProperty("nnPort", this.conf.getNameNodePort() + "");

        String jobDir = String.format("./jobs/%s", conf.getJobId());
        String jobConf = String.format("%s/config.properties", jobDir);
        Files.createDirectories(Paths.get(jobDir));
        props.store(new FileOutputStream(jobConf), new Date().toString());

        if (__debug) {
            new JobClient().run(jobDir);
        } else {
            ProcessBuilder pb = new ProcessBuilder();
            pb.directory(new File(jobDir));
            pb.command("java", "-cp", "job.jar", "car.processing.engine.JobClient", conf.getJobId());
        }
    }
}