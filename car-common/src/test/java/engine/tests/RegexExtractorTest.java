package engine.tests;

import car.app.service.RegexService;
import car.engine.processor.Dataset;
import car.engine.rules.LogMessageExtract;
import car.engine.rules.RegexExtractor;
import car.engine.store.Document;
import car.util.GsonMaker;
import com.google.gson.Gson;
import org.junit.Test;

import java.util.Arrays;

public class RegexExtractorTest {
    @Test
    public void testRegexExtractor() {

        String json1, json2;

        {
            json1 = "{\n" +
                    "    \"type\": \"regexv2\",\n" +
                    "    \"name\": \"test\",\n" +
                    "    \"exampleTexts\": [\n" +
                    "        {\n" +
                    "            \"txt\": {\"_msg\": \"service abcd started on node 1\"},\n" +
                    "            \"selections\": {\n" +
                    "                \"_msg\": [\n" +
                    "                    {\n" +
                    "                        \"start\": 8,\n" +
                    "                        \"length\": 4,\n" +
                    "                        \"name\": \"servicename\",\n" +
                    "                        \"ignore\": false,\n" +
                    "                        \"regexTokenNames\": [\n" +
                    "                            \"string/string\"\n" +
                    "                        ],\n" +
                    "                        \"type\": {\n" +
                    "                            \"type\": \"stringftype\"\n" +
                    "                        }\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"start\": 29,\n" +
                    "                        \"length\": 1,\n" +
                    "                        \"name\": \"nodeid\",\n" +
                    "                        \"ignore\": false,\n" +
                    "                        \"regexTokenNames\": [\n" +
                    "                            \"string/string\"\n" +
                    "                        ],\n" +
                    "                        \"type\": {\n" +
                    "                            \"type\": \"numberftype\"\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            }\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
        }

        {
            json2 = "{\n" +
                    "    \"type\": \"regexv2\",\n" +
                    "    \"name\": \"test2\",\n" +
                    "    \"exampleTexts\": [\n" +
                    "        {\n" +
                    "            \"txt\": {\n" +
                    "                \"_msg\": \"service abcd started on host xyz.com:8080\"\n" +
                    "            },\n" +
                    "            \"selections\": {\n" +
                    "                \"_msg\": [\n" +
                    "                    {\n" +
                    "                        \"start\": 8,\n" +
                    "                        \"length\": 4,\n" +
                    "                        \"name\": \"servicename\",\n" +
                    "                        \"ignore\": false,\n" +
                    "                        \"regexTokenNames\": [\n" +
                    "                            \"string/string\"\n" +
                    "                        ],\n" +
                    "                        \"type\": {\n" +
                    "                            \"type\": \"stringftype\"\n" +
                    "                        }\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"start\": 29,\n" +
                    "                        \"length\": 7,\n" +
                    "                        \"name\": \"host\",\n" +
                    "                        \"ignore\": false,\n" +
                    "                        \"regexTokenNames\": [\n" +
                    "                            \"string/string\"\n" +
                    "                        ],\n" +
                    "                        \"type\": {\n" +
                    "                            \"type\": \"stringftype\"\n" +
                    "                        }\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"start\": 37,\n" +
                    "                        \"length\": 4,\n" +
                    "                        \"name\": \"port\",\n" +
                    "                        \"ignore\": false,\n" +
                    "                        \"regexTokenNames\": [\n" +
                    "                            \"number/integer\"\n" +
                    "                        ],\n" +
                    "                        \"type\": {\n" +
                    "                            \"type\": \"numberftype\"\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"txt\": {\n" +
                    "                \"_msg\": \"a service abcd started at xyz.com:8080 on monday\"\n" +
                    "            },\n" +
                    "            \"selections\": {\n" +
                    "                \"_msg\": [\n" +
                    "                    {\n" +
                    "                        \"start\": 10,\n" +
                    "                        \"length\": 4,\n" +
                    "                        \"name\": \"servicename\",\n" +
                    "                        \"ignore\": false,\n" +
                    "                        \"regexTokenNames\": [\n" +
                    "                            \"string/string\"\n" +
                    "                        ],\n" +
                    "                        \"type\": {\n" +
                    "                            \"type\": \"stringftype\"\n" +
                    "                        }\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"start\": 26,\n" +
                    "                        \"length\": 7,\n" +
                    "                        \"name\": \"host\",\n" +
                    "                        \"ignore\": false,\n" +
                    "                        \"regexTokenNames\": [\n" +
                    "                            \"string/string\"\n" +
                    "                        ],\n" +
                    "                        \"type\": {\n" +
                    "                            \"type\": \"stringftype\"\n" +
                    "                        }\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"start\": 34,\n" +
                    "                        \"length\": 4,\n" +
                    "                        \"name\": \"port\",\n" +
                    "                        \"ignore\": false,\n" +
                    "                        \"regexTokenNames\": [\n" +
                    "                            \"number/integer\"\n" +
                    "                        ],\n" +
                    "                        \"type\": {\n" +
                    "                            \"type\": \"numberftype\"\n" +
                    "                        }\n" +
                    "                    },\n" +
                    "                    {\n" +
                    "                        \"start\": 42,\n" +
                    "                        \"length\": 6,\n" +
                    "                        \"name\": \"weekday\",\n" +
                    "                        \"ignore\": false,\n" +
                    "                        \"regexTokenNames\": [\n" +
                    "                            \"string/string\"\n" +
                    "                        ],\n" +
                    "                        \"type\": {\n" +
                    "                            \"type\": \"stringftype\"\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            }\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
        }


        System.out.println(json1);
        System.out.println(json2);

        Gson gson = GsonMaker.getGson();
        LogMessageExtract r1 = gson.fromJson(json1, LogMessageExtract.class);
        LogMessageExtract r2 = gson.fromJson(json2, LogMessageExtract.class);

        System.out.println(r1);
        System.out.println(r2);

        RegexExtractor re = new RegexExtractor();
        re.init(Arrays.asList(r1, r2), RegexService.getRegexTokens());

        boolean ret;

        Document doc1 = new Document("");
        ret = re.eval(r1.getExampleTexts().get(0).getTxt(), doc1);
        System.out.println(ret);
        System.out.println(doc1);

        Document doc2 = new Document("");
        ret = re.eval(r2.getExampleTexts().get(0).getTxt(), doc2);
        System.out.println(ret);
        System.out.println(doc2);

        Document doc3 = new Document("");
        ret = re.eval(r2.getExampleTexts().get(1).getTxt(), doc3);
        System.out.println(ret);
        System.out.println(doc3);

    }

}
