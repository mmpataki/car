package car.engine.store;

import car.engine.recordreader.RecordReader;
import car.engine.rules.Rule;
import car.engine.rules.types.DocField;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public abstract class Store<ResponseType, InputType, QueryType> {

    String id;

    public Store(String id) {
        this.id = id;
    }

    public abstract void init() throws Exception;

    public abstract ResponseType search(QueryType q) throws Exception;

    public abstract void put(InputType doc) throws Exception;

    public abstract void put(List<InputType> docs) throws Exception;

    public abstract void close() throws Exception;

    public abstract void delete() throws Exception;

    public abstract Object getMetadata() throws Exception;

    public abstract void reportFields(Map<String, List<DocField>> fields);
}
