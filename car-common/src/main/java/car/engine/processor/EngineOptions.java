package car.engine.processor;

import car.common.CarProcessingConfig;
import car.engine.models.SearchQuery;
import car.engine.models.SearchResponse;
import car.engine.query.Query;
import car.engine.recordreader.RecordReader;
import car.engine.rules.RuleManager;
import car.engine.store.Document;
import car.engine.store.Store;
import car.engine.store.StoreResponse;
import lombok.Builder;
import lombok.Data;

import java.io.InputStream;
import java.util.List;

@Data
@Builder
public class EngineOptions {
    long byteLimit;
    String fileName, dsetName;
    String logTypeGroup, logType;
    InputStream detectStream;
    RecordReader recordReader;
    RuleManager ruleProvider;
    CarProcessingConfig conf;
    boolean index, detect, ingest;
    List<String> possibleLogTypeGroups;
    Store<StoreResponse, Document, Query> store;
    Store<SearchResponse, Document, SearchQuery> indexStore;
}
