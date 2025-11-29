package car.engine.util;

import car.util.GsonMaker;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonUtil {
    public static void save(String path, Object obj) throws IOException {
        Gson gson = GsonMaker.getGson();
        try (FileWriter fw = new FileWriter(path)) {
            gson.toJson(obj, fw);
        }
    }

    public static <T> T read(String path, Class<T> klass) throws IOException {
        Gson gson = GsonMaker.getGson();
        try (FileReader fr = new FileReader(path)) {
            return gson.fromJson(fr, klass);
        }
    }
}
