package car.repos;

import car.engine.processor.Dataset;

import java.io.IOException;
import java.util.List;

public interface DatasetRepo {

    public Dataset findById(String id) throws Exception;

    public void save(Dataset ds) throws IOException;

    List<Dataset> findByIdOrDescription(String q) throws Exception;

}
