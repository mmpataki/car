package car.engine.processor;

import car.common.CarProcessingConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Getter
public abstract class ProcessingEngine {
    public abstract void process(Dataset dset, String filePath, boolean detect, boolean index, boolean ingest) throws Exception;
}
