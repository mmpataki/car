package car.engine.rules.types;

import car.util.TypeName;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@TypeName(name = "stringftype", displayName = "string", description = "string")
public class StringType extends FieldType<String> {
}
