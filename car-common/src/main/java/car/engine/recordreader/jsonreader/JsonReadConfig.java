package car.engine.recordreader.jsonreader;

import car.engine.recordreader.ReadConfig;
import car.engine.recordreader.StructuredRecordReader;
import car.engine.rules.types.DocField;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TypeName(name = "json", displayName = "JSON config", description = "")
public class JsonReadConfig extends ReadConfig {

    Map<String, DocField> fieldMappings;

    List<Map<String, Object>> examples;

    @Override
    public StructuredRecordReader buildReader() {
        return new JsonReader(this);
    }
}
