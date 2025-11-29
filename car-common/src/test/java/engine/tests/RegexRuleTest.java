package engine.tests;

import car.engine.processor.Dataset;
import car.engine.processor.MultiRegexRuleEvaluator;
import car.engine.rules.RegexRule;
import car.engine.rules.Rule;
import car.engine.rules.types.DocField;
import car.engine.rules.types.StringType;
import car.engine.store.Document;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class RegexRuleTest {

    @Test
    public void testRule() throws Exception {
        String message = "CS [mycs] started";
        Rule r = RegexRule.builder()
                .pattern("CS \\[(.*)\\] started")
                .groupNameMap(new HashMap<Integer, DocField>() {{
                    put(1, DocField.builder().name("csname").type(new StringType()).build());
                }})
                .build();

        r.init(null);
        Document doc = new Document("foo");
        Dataset ds = new Dataset();
        ds.setId("1");
        r.eval(message, doc, ds);
        Assert.assertEquals(doc.get("csname"), "mycs");
    }

    @Test
    public void testRuleEvaluator() {
        String message = "CS [mycs] is eating [food]";
        Rule r1 = RegexRule.builder()
                .pattern("CS \\[(.*?)\\] started")
                .groupNameMap(new HashMap<Integer, DocField>() {{
                    put(1, DocField.builder().name("csname").type(new StringType()).build());
                }})
                .build();

        Rule r2 = RegexRule.builder()
                .pattern("CS \\[(.*?)\\] shutdown")
                .groupNameMap(new HashMap<Integer, DocField>() {{
                    put(1, DocField.builder().name("csname").type(new StringType()).build());
                }})
                .build();

        Rule r3 = RegexRule.builder()
                .pattern("CS \\[(.*?)\\] is eating \\[(.*?)\\]")
                .groupNameMap(new HashMap<Integer, DocField>() {{
                    put(1, DocField.builder().name("csname").type(new StringType()).build());
                    put(2, DocField.builder().name("snack").type(new StringType()).build());
                }})
                .build();

        Rule r4 = RegexRule.builder()
                .pattern("CS \\[(.*?)\\] is drinking \\[(.*?)\\]")
                .groupNameMap(new HashMap<Integer, DocField>() {{
                    put(1, DocField.builder().name("csname").type(new StringType()).build());
                    put(2, DocField.builder().name("beverage").type(new StringType()).build());
                }})
                .build();

        MultiRegexRuleEvaluator mre = new MultiRegexRuleEvaluator();
        mre.init("foo", Arrays.asList(r1, r2, r3, r4));
        Document doc = mre.eval(message);


        Assert.assertEquals(doc.get("csname"), "mycs");
        Assert.assertEquals(doc.get("snack"), "food");
    }

    @Test
    public void benchMark() {

        Random r = new Random();
        String data[] = new String[100];
        List<Rule> rules = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            int len = r.nextInt(99) + 1;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; j++) {
                for (int k = 0; k < r.nextInt(5); k++) {
                    sb.append((char)(r.nextInt(26) + 'a'));
                }
                sb.append(' ');
            }
            data[i] = sb.toString();
            rules.add(RegexRule.builder().pattern(data[i]).build());
        }

        MultiRegexRuleEvaluator mre = new MultiRegexRuleEvaluator();
        mre.init("dummy", rules);

        for (String datum : data) {
            for (String s : data) {
                mre.eval(datum);
            }
        }

    }

}
