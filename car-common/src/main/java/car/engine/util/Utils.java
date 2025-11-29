package car.engine.util;

import java.util.Collections;
import java.util.Map;

public class Utils {
    public static Map<String, Object> wrapLine(String line) {
        return Collections.singletonMap("_msg", line);
    }

}
