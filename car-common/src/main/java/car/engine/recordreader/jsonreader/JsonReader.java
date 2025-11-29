package car.engine.recordreader.jsonreader;

import car.app.service.RegexService;
import car.engine.processor.TrackingInputStream;
import car.engine.recordreader.StructuredRecordReader;
import car.engine.rules.types.DocField;
import car.engine.util.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JsonReader extends StructuredRecordReader {

    JsonReadConfig conf;
    List<Pair<String[], DocField>> fieldMappers = new LinkedList<>();

    Gson gson;
    JsonElement je;
    BufferedReader br;

    public JsonReader(JsonReadConfig config) {
        this.conf = config;
    }

    @Override
    public void setInputStream(TrackingInputStream inputStream) {
        super.setInputStream(inputStream);
        br = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public void init(RegexService.RegexTokens regexTokens) throws Exception {
        gson = new GsonBuilder().setLenient().create();
        conf.getFieldMappings().forEach((fPath, docF) -> {
            if (fPath.isEmpty() || docF == null) return;
            docF.init(regexTokens);
            fieldMappers.add(new Pair<>(fPath.split("/"), docF));
        });
    }

    @SneakyThrows
    @Override
    public boolean _hasNext() {
        String line;
        while ((line = br.readLine()) != null) {
            try {
                je = gson.fromJson(line, JsonElement.class);
                if (je == null)
                    continue;
                return true;
            } catch (Exception jse) {
                //log.trace("Error in json processing", jse);
            }
        }
        return false;
    }

    @SneakyThrows
    @Override
    public Map<String, Object> _next() {
        Map<String, Object> ret = new HashMap<>();
        for (Pair<String[], DocField> fm : fieldMappers) {
            String[] jsonPathEntries = fm.getFirst();
            DocField df = fm.getSecond();
            JsonElement elem = je;
            for (int i = 1; elem != null && i < jsonPathEntries.length - 1; i++) {
                elem = elem.getAsJsonObject().get(jsonPathEntries[i]);
            }
            if(elem == null || !elem.isJsonObject())
                continue;
            elem = elem.getAsJsonObject().get(jsonPathEntries[jsonPathEntries.length - 1]);
            String val;
            if (elem != null) {
                if (elem.isJsonPrimitive())
                    val = elem.getAsString();
                else
                    val = elem.toString();
                ret.put(df.getName(), df.getType().convert(val));
            }
        }
        ret.put("_msg", ret.values().stream().map(Object::toString).collect(Collectors.joining(", ")));
        log.trace("Read message: {}", ret);
        return ret;
    }

    @Override
    public List<String> getFieldNames() {
        return conf.getFieldMappings().values().stream().map(DocField::getName).collect(Collectors.toList());
    }

    @Override
    public List<DocField> getFields() {
        return new LinkedList<>(conf.getFieldMappings().values());
    }

}
