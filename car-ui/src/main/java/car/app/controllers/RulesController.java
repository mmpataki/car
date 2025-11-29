package car.app.controllers;

import car.app.service.RuleService;
import car.engine.rules.LogType;
import car.engine.rules.LogTypeGroup;
import car.engine.rules.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/log-type-groups")
public class RulesController {

    @Autowired
    RuleService RS;

    @GetMapping
    public List<LogTypeGroup> getLogTypeGroups() {
        return RS.getLogTypeGroups();
    }

    @PostMapping
    public void addLogTypeGroup(@RequestBody LogTypeGroup grp) throws IOException {
        RS.registerLogTypeGroup(grp);
    }

    @GetMapping("{group}/logtypes")
    public List<LogType> getLogTypes(@PathVariable String group) {
        return RS.getLogTypes(group);
    }

    @GetMapping("{group}/logtypes/{logtype}")
    public LogType getLogTypes(@PathVariable String group, @PathVariable String logtype) {
        return RS.getLogType(group ,logtype);
    }

    @PostMapping("{group}/logtypes")
    public void addLogType(@PathVariable String group, @RequestBody LogType type) throws IOException {
        RS.addLogType(group, type);
    }

    @PostMapping("{group}/logtypes/{type}/rules")
    public void addRule(@PathVariable String group, @PathVariable String type, @RequestBody Rule rule) throws Exception {
        RS.addRule(group, type, rule);
    }

    @GetMapping("{group}/logtypes/{type}/rules")
    public List<Rule> getRules(@PathVariable String group, @PathVariable String type) throws Exception {
        return RS.getRules(group, type);
    }

    @DeleteMapping("{group}/logtypes/{type}/rules/{rule}")
    public void deleteRule(@PathVariable String group, @PathVariable String type, @PathVariable String rule) throws Exception {
        RS.deleteRule(group, type, rule);
    }

    @PostMapping("sync")
    public void sync() throws Exception {
        RS.sync();
    }

}
