package car.app.controllers;

import car.app.service.RegexService;
import car.engine.processor.Dataset;
import car.engine.rules.LogMessageExtract;
import car.engine.rules.RegexExtractor;
import car.engine.rules.RegexExtractor.RegexMatch;
import car.engine.store.Document;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class RegexController {

    @Data
    static class RegexQuery {
        LogMessageExtract rule;
        List<Map<String, Object>> txts;
    }

    @Autowired
    RegexService RS;

    @GetMapping("/regex/tokens")
    public RegexService.RegexTokens getRegexTokens() {
        return RS.getRegexTokens();
    }

    @Data
    public static class RegexTokenSaveReq {
        String group, key;
        RegexService.RegexToken token;
    }

    @PostMapping("/regex/tokens")
    public void saveRegexTokens(@RequestBody RegexTokenSaveReq req) throws Exception {
        RS.saveRegexToken(req.group, req.key, req.token);
    }

    @PostMapping("/regex/match")
    public List<RegexExtractor.RegexMatch> match(@RequestBody RegexQuery q) {
        List<RegexExtractor.RegexMatch> ret = new ArrayList<>();
        RegexExtractor ex = new RegexExtractor();
        ex.setDebuggingEnabled(true);
        ex.init(Collections.singletonList(q.getRule()), RS.getRegexTokens());
        for (Map<String, Object> txt : q.getTxts()) {
            Document d = new Document("");
            ex.eval(txt, d);
            ret.add((RegexMatch) d.getDebugData().get("regexMatchBounds"));
        }
        return ret;
    }


}
