package car.engine.rules.types;

import car.app.service.RegexService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocField {
    String name;
    int start, length;
    boolean ignore;
    FieldType type;
    List<String> regexTokenNames;

    public String getName() {
        if (name == null)
            name = "ign" + System.nanoTime();
        return name;
    }

    public void init(RegexService.RegexTokens regexTokens) {
        FieldType type = getType();
        getRegexTokenNames().forEach(tokName -> {
            RegexService.RegexToken tok = regexTokens.getRegexFor(tokName);
            if (tok.getProps() != null) {
                tok.getProps().forEach(prop -> {
                    try {
                        Field f = type.getClass().getDeclaredField(prop.getKey());
                        f.setAccessible(true);
                        f.set(type, prop.getValue());
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        // ignore
                        log.debug("", e);
                    }
                });
            }
        });
    }
}
