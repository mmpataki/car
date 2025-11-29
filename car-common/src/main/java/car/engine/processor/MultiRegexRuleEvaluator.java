package car.engine.processor;

import car.engine.rules.RegexRule;
import car.engine.rules.Rule;
import car.engine.store.Document;
import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

import java.util.List;

public class MultiRegexRuleEvaluator implements RuleEvaluator {

    private List<Rule> ruleList;
    private String txtPrefix;
    private Pattern p;
    private String ruleNamePrefix;

    @Override
    public void init(String ruleNamePrefix, List<Rule> ruleList) {

        this.ruleNamePrefix = ruleNamePrefix;
        this.ruleList = ruleList;

        /* final pattern */
        StringBuilder psb = new StringBuilder("(");

        /* text prefix */
        StringBuilder tpsb = new StringBuilder();

        int i = 0;

        for (Rule rule : ruleList) {

            String pattern = ((RegexRule) rule).getPattern();

            if (i != 0)
                psb.append('|');

            psb.append(".*?(").append(i).append(").*?\\|\\|").append(pattern);

            tpsb.append(i).append(",");

            i++;

        }
        p = Pattern.compile(psb.append(')').toString());
        txtPrefix = tpsb.append("||").toString();

        System.out.println(psb.toString());
        System.out.println(txtPrefix);
    }

    @Override
    public Document eval(String msg) {

        String txt = txtPrefix + msg;
        Matcher matcher = p.matcher(txt);

        if (matcher.matches()) {

            /* find the rule id */
            int ruleIdGrp = 2;
            while (ruleIdGrp < matcher.groupCount() && matcher.group(ruleIdGrp) == null)
                ruleIdGrp++;

            if (ruleIdGrp >= matcher.groupCount())
                return null;

            RegexRule rule = (RegexRule) ruleList.get(Integer.parseInt(matcher.group(ruleIdGrp)));
            Document doc = new Document(ruleNamePrefix + "_" + rule.getName());

            int nameIdx = 1;
            for (int i = ruleIdGrp + 1; i <= matcher.groupCount(); i++) {
                if(matcher.group(i) == null)
                    continue;
                doc.put(rule.getGroupNameMap().get(nameIdx).getName(), matcher.group(i));
                nameIdx++;
            }

            return doc;

        }

        return null;
    }
}
