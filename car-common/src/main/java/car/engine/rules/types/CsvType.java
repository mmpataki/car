package car.engine.rules.types;

import car.util.TypeName;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@TypeName(name = "csvftype", displayName = "csv", description = "CSV records")
public class CsvType extends FieldType<Map<String, Object>> {
    Map<String, DocField> fields;
    transient static Gson G = new Gson();

    @Override
    public Map<String, Object> convert(String v) throws ParseException {
        JsonObject json = G.fromJson(v, JsonObject.class);
        Map<String, Object> ret = new HashMap<>();
        for (Map.Entry<String, DocField> entry : fields.entrySet()) {
            String key = entry.getKey();
            DocField field = entry.getValue();
            if (json.has(key) && !field.isIgnore()) {
                ret.put(
                        field.getName(),
                        field.getType().convert(json.get(key).getAsString())
                );
            }
        }
        return ret;
    }

    public boolean isMultiValued() {
        return true;
    }
}
