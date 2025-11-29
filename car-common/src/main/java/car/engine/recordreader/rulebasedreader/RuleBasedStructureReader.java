package car.engine.recordreader.rulebasedreader;

import car.app.service.RegexService;
import car.engine.processor.Dataset;
import car.engine.recordreader.StructuredRecordReader;
import car.engine.rules.RegexExtractor;
import car.engine.rules.Rule;
import car.engine.rules.types.DocField;
import car.engine.store.Document;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static car.engine.util.Utils.wrapLine;

@Slf4j
public class RuleBasedStructureReader extends StructuredRecordReader {

    transient List<Rule> rules;
    transient String line, lastLine = null;
    transient private BufferedReader br;
    int done = 0;
    RegexExtractor ex = new RegexExtractor();

    public List<Rule> getRules() {
        return rules;
    }

    public RuleBasedStructureReader(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public void init(RegexService.RegexTokens regexTokens) throws IOException {
        br = new BufferedReader(new InputStreamReader(getInputStream()));
        ex.init(rules, regexTokens);

        Document doc = new Document("");
        Dataset ds = new Dataset();

        do {
            lastLine = br.readLine();
            if (lastLine != null && ex.eval(wrapLine(lastLine), doc))
                break;
            else {
                log.warn("Skipping: " + lastLine);
            }
        } while (lastLine != null);
        if (lastLine == null)
            done++;
        advance();
    }

    @Override
    public boolean _hasNext() {
        return (done != 2);
    }

    @Override
    public Map<String, Object> _next() {
        String ret = null;
        if (done == 2) {
            throw new IllegalArgumentException("no elements left, use hasNext() before calling next()");
        }
        Document d = new Document("");
        if (line != null) {
            ret = line.trim();
            ex.eval(wrapLine(ret), d);
            d.remove("_rule");
            d.remove("_matchedRule");
            d.put("_msg", ret);
            advance();
            if (lastLine == null)
                done++;
        }
        return d.getData();
    }

    private void advance() {
        try {
            StringBuilder sb = new StringBuilder(lastLine == null ? "" : lastLine);
            Document doc = new Document("");
            do {
                lastLine = br.readLine();
                if (lastLine != null) {
                    if (ex.eval(wrapLine(lastLine), doc)) {
                        break;
                    }
                    sb.append(lastLine).append("\n");
                }
            } while (lastLine != null);
            line = sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getFieldNames() {
        return getRules().stream().flatMap(rule -> rule.getFields().stream().map(DocField::getName)).collect(Collectors.toList());
    }

    @Override
    public List<DocField> getFields() {
        return this.rules.stream().flatMap(rule -> rule.getFields().stream()).collect(Collectors.toList());
    }
}
