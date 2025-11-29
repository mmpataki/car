package car.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

@Slf4j
public class GsonMaker {

    private static Gson __instance;

    public static <T> void doRegister(Class<T> clz, GsonBuilder gb, Reflections reflections) {
        RuntimeTypeAdapterFactory<T> rtafTA = RuntimeTypeAdapterFactory.of(clz, "type", true);
        reflections.getSubTypesOf(clz).forEach(ta -> {
            try {
                TypeName tn = ta.getAnnotation(TypeName.class);
                if (tn != null) {
                    log.info("registering subtypes of {} : {} = {}", clz.getName(), tn.name() ,ta.getName());
                    rtafTA.registerSubtype(ta, tn.name());
                }
            } catch (Exception e) {
                log.error("Register failed for " + ta);
            }
        });
        gb.registerTypeAdapterFactory(rtafTA);
    }

    public static Gson getGson(Class<?>... classes) {
        if(__instance != null)
            return __instance;
        GsonBuilder gb = new GsonBuilder();
        gb.serializeSpecialFloatingPointValues();
        Reflections reflections = new Reflections("car", new SubTypesScanner(false));
        log.info(reflections.getAllTypes().toString());
        reflections.getSubTypesOf(Object.class).forEach(clz -> {
            if (clz.isAnnotationPresent(BaseType.class)) {
                doRegister(clz, gb, reflections);
            }
        });
        gb.registerTypeAdapterFactory(PostConstructTypeAdapterFactory.getPostConstructTypeAdapterFactory());
        __instance = gb.create();
        return __instance;
    }

}
