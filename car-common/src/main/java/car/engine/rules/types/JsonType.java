package car.engine.rules.types;

import car.util.TypeName;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.StringReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@TypeName(name = "jsonftype", displayName = "json", description = "json")
public class JsonType extends FieldType<Map<String, Object>> {
    Map<String, DocField> fields;
    transient static Gson G = new Gson();

    @Override
    public Map<String, Object> convert(String v) throws ParseException {
        JsonReader jr = new JsonReader(new StringReader(v));
        jr.setLenient(true);
        JsonObject json = G.fromJson(jr, JsonObject.class);
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

    @Override
    public List<DocField> getSubFields() {
        List<DocField> ret = new LinkedList<>();
        for (DocField field : fields.values()) {
            if(field.getType().isMultiValued())
                ret.addAll(field.getType().getSubFields());
            else
                ret.add(field);
        }
        return ret;
    }
}
