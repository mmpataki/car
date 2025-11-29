//package engine.tests;
//
//import car.engine.detector.Detector;
//import car.engine.detector.FileNameBasedDetector;
//import car.engine.processor.AnalysisStatus;
//import car.engine.processor.Dataset;
//import car.engine.processor.InMemProcessor;
//import car.engine.processor.ProcessorOptions;
//import car.engine.query.Op;
//import car.engine.query.Query;
//import car.engine.recordreader.unstructured.LineRecordReader;
//import car.engine.rules.LogType;
//import car.engine.rules.LogTypeGroup;
//import car.engine.rules.RegexRule;
//import car.engine.rules.Rule;
//import car.engine.store.Document;
//import car.engine.store.MongoStore;
//import car.engine.store.Store;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//
//public class InMemProcessorTest {
//
//    @Test
//    public void testInmemProcessor() throws Exception {
//
//        Store store = new MongoStore("test");
//
//        Rule r1 = RegexRule.builder().pattern("CS \\[(.*)\\] started").groupNameMap(new HashMap<Integer, RegexRule.DocField>() {{
//            put(1, RegexRule.DocField.builder().name("csname").type(new RegexRule.StringType()).build());
//        }}).build();
//
//        Rule r2 = RegexRule.builder().pattern("CS \\[(.*)\\] finished").groupNameMap(new HashMap<Integer, RegexRule.DocField>() {{
//            put(1, RegexRule.DocField.builder().name("csname").type(new RegexRule.StringType()).build());
//        }}).build();
//
//        Detector d1 = FileNameBasedDetector.builder().pattern(".*test.in.*").build();
//        Detector d2 = FileNameBasedDetector.builder().pattern(".*test.out.*").build();
//
//
//        LogType inlog = LogType.builder()
//                .detectors(Arrays.asList(d1))
//                .rules(Arrays.asList(r1, r2))
//                .recordReader(new LineRecordReader())
//                .build();
//
//        LogType outlog = LogType.builder()
//                .detectors(Arrays.asList(d2))
//                .rules(Arrays.asList(r1, r2))
//                .recordReader(new LineRecordReader())
//                .build();
//
//        LogTypeGroup ltgroup = LogTypeGroup.builder()
//                .name("edc")
//                .logTypes(Arrays.asList(inlog, outlog))
//                .description("enterprise data catalog")
//                .build();
//
//        InMemProcessor processor = new InMemProcessor();
//        ProcessorOptions opts = ProcessorOptions.builder()
//                .store(store)
//                .dataset(
//                        Dataset.builder()
//                                .logTypeGroups(Collections.singletonList(ltgroup))
//                                .files(Arrays.asList("src/test/resources/inmemprocessortest.in1.txt", "src/test/resources/inmemprocessortest.in2.txt"))
//                                .detailStatus(new AnalysisStatus())
//                                .name("dummy")
//                                .build()
//                )
//                .build();
//
//        processor.process(opts);
//
//        Query q1 = Query.builder().criteria(new Op.EqOp().with("csname", "mycs")).build();
//        List<Document> results1 = store.search(q1);
//        System.out.println(results1);
//        Assert.assertEquals(2, results1.size());
//
//        Query q2 = Query.builder().criteria(new Op.EqOp().with("csname", "mysecondcs")).build();
//        List<Document> results2 = store.search(q2);
//        System.out.println(results2);
//        Assert.assertEquals(2, results2.size());
//
//    }
//
//}
