package car.engine.store;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class Document {

    /* the rule which brought in this doc */
    Map<String, Object> data = new HashMap<>();

    /* this can contain any kind of debug info, key is context of debugging */
    Map<String, Object> debugData = new HashMap<>();

    public boolean isEmpty() {
        return data.size() < 2;
    }

    public Object get(String key) {
        return data.get(key);
    }

    public void remove(String key) {
        data.remove(key);
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public void putAll(Map<String, Object> map) {
        data.putAll(map);
    }

    public Set<String> keys() {
        return data.keySet();
    }

    public Document(String type) {
        setType(type);
    }

    public void setType(String type) {
        put("_rule", type);
    }

    public void clear() {
        data.clear();
    }

    public String getType() {
        return (String) get("_rule");
    }

}
