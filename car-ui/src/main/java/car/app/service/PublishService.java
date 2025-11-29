package car.app.service;

import car.engine.rules.LogType;
import car.engine.rules.LogTypeGroup;
import car.engine.rules.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PublishService {

    @Autowired
    RuleService RS;

    public void postLogTypeGroup(LogTypeGroup grp) throws IOException {
        RS.registerLogTypeGroup(grp);
    }

    public void postLogType(String group, LogType typ) throws Exception {
        RS.addLogType(group, typ);
    }

    public void postRule(String group, String type, Rule rule) throws Exception {
        RS.addRule(group, type, rule);
    }

}
