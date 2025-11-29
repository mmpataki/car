package car.engine.recordreader.csvreader;

import car.app.service.RegexService;
import car.engine.processor.TrackingInputStream;
import car.engine.recordreader.StructuredRecordReader;
import car.engine.rules.types.DateType;
import car.engine.rules.types.DocField;
import car.engine.rules.types.StringType;
import junit.framework.TestCase;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class CsvReaderTest extends TestCase {

    public void testCsv() throws Exception {

        Map<String, DocField> mapping = new HashMap<>();
        mapping.put("timestamp", DocField.builder().name("ts").type(DateType.builder().format("yyyy-MM-dd\u0027T\u0027HH:mm:ss.SSSX").build()).build());
        mapping.put("serviceName", DocField.builder().ignore(true).build());
        mapping.put("level", DocField.builder().ignore(true).build());
        mapping.put("messageId", DocField.builder().ignore(true).build());
        mapping.put("componentId", DocField.builder().ignore(true).build());
        mapping.put("message", DocField.builder().name("msg").type(StringType.builder().build()).build());
        mapping.put("userId", DocField.builder().ignore(true).build());
        mapping.put("threadName", DocField.builder().ignore(true).build());

        CsvReadConfig conf = CsvReadConfig.builder()
                .quoteChar('"')
                .separator(',')
                .firstRowHeader(true)
                .fieldMappings(mapping)
                .build();

        StructuredRecordReader reader = conf.buildReader();
        reader.setInputStream(new TrackingInputStream(new FileInputStream("./csvtest.csv")));
        reader.init(RegexService.getRegexTokens());
        while (reader.hasNext()) {
            System.out.println(reader.next());
        }
    }
}