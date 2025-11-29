package car.engine.rules.types;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@TypeName(name = "listftype", displayName = "list", description = "list")
public class ListType extends FieldType<List<String>> {
    String delimiter;

    @Override
    public List<String> convert(String v) throws ParseException {
        return Arrays.asList(v.split(delimiter));
    }
}
