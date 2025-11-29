package car.engine.rules.types;

import car.util.TypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@TypeName(name = "numberftype", displayName = "number", description = "number")
public class NumberType extends FieldType<Double> {
    @Override
    public Double convert(String v) {
        return Double.parseDouble(v);
    }
}
