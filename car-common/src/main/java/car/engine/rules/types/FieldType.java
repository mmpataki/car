package car.engine.rules.types;

import car.util.BaseType;
import car.util.TypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@BaseType
@TypeName(name = "fieldtype", displayName = "fieldtype", description = "field type")
public abstract class FieldType<T> {
    String type;

    public boolean isMultiValued() {
        return false;
    }

    public T convert(String v) throws ParseException {
        return (T) v;
    }

    /* set when multivalued field */
    public List<DocField> getSubFields() {
        return Collections.emptyList();
    }
}
