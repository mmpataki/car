package car.engine.recordreader.csvreader;

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
@TypeName(name = "csv", displayName = "CSV config", description = "")
public class CsvReadConfig extends ReadConfig {

    Character separator = ',', quoteChar = '"';

    Map<String, DocField> fieldMappings;

    List<Map<String, String>> examples;

    boolean firstRowHeader = true;

    @Override
    public StructuredRecordReader buildReader() {
        return new CsvReader(this);
    }
}
