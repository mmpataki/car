package car.engine.rules;

import car.app.service.RegexService;
import car.engine.store.Document;

import java.util.List;
import java.util.Map;

public abstract class Extractor {

    public abstract void init(List<Rule> extracts, RegexService.RegexTokens regexTokens);

    public abstract boolean eval(Map<String, Object> message, Document doc);

}
