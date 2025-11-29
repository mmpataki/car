package engine.tests;

import car.engine.query.Query;
import car.engine.recordreader.RecordReader;
import car.engine.rules.Rule;
import car.engine.rules.types.DocField;
import car.engine.store.BatchingStore;
import car.engine.store.Document;
import car.engine.store.Store;
import car.engine.store.StoreResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchingStoreTest {

    @Test
    public void test() throws Exception {

        final AtomicInteger ai = new AtomicInteger(0);

        Store d = new Store<StoreResponse, Document, Query>("dummy") {
            @Override
            public void init() {

            }

            @Override
            public StoreResponse search(Query q) {
                return null;
            }

            @Override
            public void put(Document doc) {
                throw new RuntimeException("Batching store is calling a non-batch method");
            }

            @Override
            public void put(List<Document> docs) {
                ai.incrementAndGet();
                System.out.println("flush: " + ai.get() + " bs: " + docs.size());
            }

            @Override
            public void close() {
            }

            @Override
            public void delete() {

            }

            @Override
            public Object getMetadata() throws Exception {
                return null;
            }

            @Override
            public void reportFields(Map<String, List<DocField>> fields) {

            }

        };
        Store s = new BatchingStore(d, 2);

        s.put(new Document("ss"));  // no flush
        Assert.assertEquals(0, ai.get());

        s.put(new Document("ss"));  // flush 1, bs: 2
        Assert.assertEquals(1, ai.get());

        s.put(new Document("ss"));  // no flush
        Assert.assertEquals(1, ai.get());

        s.put(Arrays.asList(new Document("ss"), new Document("ss")));   // flush 2, bs: 3
        Assert.assertEquals(2, ai.get());

        s.put(new Document("ss"));  // no flush
        Assert.assertEquals(2, ai.get());

        s.close();  // flush
        Assert.assertEquals(3, ai.get());

    }

}
