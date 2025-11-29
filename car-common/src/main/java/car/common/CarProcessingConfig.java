package car.common;

import lombok.Getter;

@Getter
public class CarProcessingConfig extends CarCommonConfig {

    @Argument(keys = {"--processor.threadpool.size"}, help = "Number of threads used to process files", defValue = "3")
    int threadPoolSize;

    @Argument(keys = {"--processor.max.msgs"}, help = "Number of max msgs to process", defValue = "-1")
    int maxMsgsToProcess;

    public CarProcessingConfig(String[] args) throws Exception {
        super(args);
    }

    public CarProcessingConfig() throws Exception {
        super();
    }
}
