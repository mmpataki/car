package car.engine.processor;

import car.engine.rules.Rule;
import car.engine.store.Document;

import java.util.List;

public interface RuleEvaluator {

    public void init(String rulePrefix, List<Rule> ruleList);

    public Document eval(String msg);

}
