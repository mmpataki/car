package car.app.controllers;

import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v2")
public class RegexControllerV2 {

    @Data
    @Builder
    static class MatchGroup {
        String txt;
        int begin, end;
    }

    @Data
    @Builder
    static class RegexMatch {
        String pattern;
        String txt;
        List<MatchGroup> groups;
    }

    @Data
    static class RegexQuery {
        String regex;
        List<String> txts;
    }

    @PostMapping("/regex/match")
    public List<RegexMatch> match(@RequestBody RegexQuery q) {

        List<RegexMatch> ret = new ArrayList<>();
        Pattern p;

        try {
            p = Pattern.compile(q.getRegex(), Pattern.MULTILINE | Pattern.DOTALL);
        } catch (Exception e) {
            log.error("Error while compiling regex", e);
            return Collections.emptyList();
        }

        for (String txt : q.getTxts()) {

            Matcher m = p.matcher(txt);
            List<MatchGroup> matches = new ArrayList<>();

            if(m.matches()) {
                for (int i = 1; i <= m.groupCount(); i++) {
                    matches.add(MatchGroup.builder().begin(m.start(i)).end(m.end(i)).txt(m.group(i)).build());
                }
            }

            ret.add(RegexMatch.builder().pattern(q.getRegex()).txt(txt).groups(matches).build());
        }

        return ret;
    }


}
