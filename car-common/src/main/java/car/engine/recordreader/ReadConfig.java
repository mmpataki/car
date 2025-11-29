package car.engine.recordreader;

import car.util.BaseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@BaseType
@AllArgsConstructor
@NoArgsConstructor
public abstract class ReadConfig {

    String type;

    public abstract StructuredRecordReader buildReader();

}
