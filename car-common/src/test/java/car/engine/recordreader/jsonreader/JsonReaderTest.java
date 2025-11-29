package car.engine.recordreader.jsonreader;

import car.app.service.RegexService;
import car.engine.processor.TrackingInputStream;
import car.engine.recordreader.StructuredRecordReader;
import car.engine.rules.types.DocField;
import car.engine.rules.types.NumberType;
import car.engine.rules.types.StringType;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;

public class JsonReaderTest {

    @Test
    public void basicTest() throws Exception {
        System.out.println(new File(".").getAbsolutePath());
        JsonReadConfig conf = JsonReadConfig.builder().fieldMappings(
                new HashMap<String, DocField>() {{
                    put("foo/bar/baz", DocField.builder().name("baz1").type(new StringType()).regexTokenNames(Arrays.asList("string/string")).build());
                    put("foo/damn", DocField.builder().name("damn1").type(new NumberType()).regexTokenNames(Arrays.asList("number/integer")).build());
                }}
        ).build();

        StructuredRecordReader jsonReader = conf.buildReader();
        jsonReader.setInputStream(new TrackingInputStream(getClass().getResourceAsStream("/jsontest.log")));
        jsonReader.init(RegexService.getRegexTokens());
        while (jsonReader.hasNext()) {
            System.out.println(jsonReader.next());
        }
    }

}
