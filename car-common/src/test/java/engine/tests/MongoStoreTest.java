//package engine.tests;
//
//import org.junit.Test;
//import car.engine.query.Op;
//import car.engine.query.Query;
//import car.engine.store.Document;
//import car.engine.store.MongoStore;
//import car.engine.store.Store;
//
//import java.io.IOException;
//import java.util.Date;
//import java.util.List;
//
//public class MongoStoreTest {
//
//    @Test
//    public void testPut() throws Exception {
//        createStore();
//    }
//
//    @Test
//    public void testSearch() throws Exception {
//        Store store = createStore();
//        Op op = new Op.GtOp().with("ts", 0);
//        Query q = Query.builder().criteria(op).build();
//        List<Document> results = store.search(q);
//        System.out.println(results);
//    }
//
//    private Store createStore() throws Exception {
//
//        Store store = new MongoStore("test");
//
//        // a process started doc
//        Document startProcDoc = new Document("ss");
//        startProcDoc.put("ts", (new Date()).getTime());
//        startProcDoc.put("pstart", true);
//        startProcDoc.put("pname", "cs");
//
//        // a process ended doc
//        Document endProcDoc = new Document("ss");
//        endProcDoc.put("pend", true);
//        endProcDoc.put("pname", "cs");
//
//        store.put(startProcDoc);
//        store.put(endProcDoc);
//
//        return store;
//    }
//
//}
