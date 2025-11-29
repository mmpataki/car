package car.processing.engine;

import car.common.Argument;
import car.common.CarCommonConfig;
import lombok.Data;

@Data
public class JobConf extends CarCommonConfig {

    @Argument(keys = {"--jobconf.job.id"}, help = "Job ID")
    String jobId;

    @Argument(keys = {"--jobconf.user.id"}, help = "User ID")
    String userId;

    @Argument(keys = {"--jobconf.dset.id"}, help = "Dataset ID")
    String datasetId;

    @Argument(keys = {"--jobconf.read.file"}, help = "File to read")
    String filePath;

    @Argument(keys = {"--jobconf.read.startpos"}, help = "Position in the file from where to read the file")
    long readStartPos;

    @Argument(keys = {"--jobconf.read.limit"}, help = "How many max bytes to read from file")
    long readLimit;

    @Argument(keys = {"--jobconf.doDetect"}, help = "Should we detect")
    boolean doDetect;

    @Argument(keys = {"--jobconf.doIndex"}, help = "Should we index")
    boolean doIndex;

    @Argument(keys = {"--jobconf.doIngest"}, help = "Should we ingest")
    boolean doIngest;

    @Argument(keys = {"--jobconf.log.type.group"}, help = "Log type group of this file")
    String logTypeGrp;

    @Argument(keys = {"--jobconf.log.type"}, help = "Log type of this file")
    String logType;

    @Argument(keys = {"--jobconf.car.url"}, help = "CAR url")
    String carUrl;

    @Argument(keys = {"--jobconf.executors"}, help = "Number of executors")
    int numExecutors;

    public JobConf(String[] args) throws Exception {
        super(args);
    }

    public JobConf() throws Exception {
        super();
    }
}
