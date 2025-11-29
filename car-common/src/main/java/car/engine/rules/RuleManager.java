package car.engine.rules;

import java.io.IOException;
import java.util.List;

public interface RuleManager {

    LogTypeGroup getLogTypeGroup(String name);

    List<LogTypeGroup> getLogTypeGroups();

    List<LogType> getLogTypes(String group);

    LogType getLogType(String group, String logtype);

    void registerLogTypeGroup(LogTypeGroup grp) throws IOException;

    void addLogType(String group, LogType type) throws IOException;

    void _addRule(String group, String type, Rule rule) throws IOException;

    void _deleteRule(String group, String type, String rule) throws IOException;

    default List<Rule> getRules(String group, String type) {
        List<Rule> rules = _getRules(group, type);
        rules.forEach(r -> {
            r.setLogTypeGroup(group);
            r.setLogType(type);
        });
        return rules;
    }

    List<Rule> _getRules(String group, String type);
}
