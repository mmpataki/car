package car.app.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RegexService {

    public static synchronized void saveRegexToken(String group, String key, RegexToken token) throws Exception {
        RegexTokens tokens = getRegexTokens();
        Map<String, RegexToken> map = tokens.computeIfAbsent(group, k -> new HashMap<>());
        map.put(key, token);
        save(tokens);
    }

    private static void save(RegexTokens tokens) throws IOException {
        try (FileWriter fw = new FileWriter("./regexTokens.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(tokens, fw);
        } catch (FileNotFoundException e) {
            log.error("Couldn't save inferrable regexes", e);
        }
    }

    @Builder
    @Data
    public static class RegexToken {
        String regex, label, type, varName;
        boolean skipForScan;
        List<AdditionalAttribute> props;
    }

    @Data
    public static class AdditionalAttribute {
        String key;
        String value;
    }

    public static class RegexTokens extends HashMap<String, Map<String, RegexToken>> {
        public RegexToken getRegexFor(String tokName) {
            try {
                String grp = tokName.substring(0, tokName.indexOf('/'));
                String key = tokName.substring(tokName.indexOf('/') + 1);
                return get(grp).get(key);
            } catch (NullPointerException npe) {
                String LBL = "REGEX:::";
                if (tokName.startsWith(LBL)) {
                    return RegexToken.builder().regex(tokName.substring(LBL.length())).label("regex").type("string").varName("t1").props(Collections.emptyList()).build();
                }
                throw new NullPointerException(tokName + " is neither a known regex token nor a regex");
            }
        }
    }

    public static RegexTokens regexes;

    static {
        load();
    }

    static void load() {
        try {
            regexes = new Gson().fromJson(new FileReader("./regexTokens.json"), RegexTokens.class);
        } catch (FileNotFoundException e) {
            log.error("Couldn't read inferrable regexes", e);
            regexes = new RegexTokens();
        }
    }

    public static RegexTokens getRegexTokens() {
        load();
        return regexes;
    }

}
