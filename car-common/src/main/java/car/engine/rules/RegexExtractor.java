package car.engine.rules;

import car.app.service.RegexService;
import car.engine.rules.types.DocField;
import car.engine.rules.types.FieldType;
import car.engine.store.Document;
import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class RegexExtractor extends Extractor {

    @Data
    @Builder
    static class MatchGroup {
        int begin, end;
    }

    @Data
    @Builder
    public static class RegexMatch {
        String pattern;
        String txt;
        List<MatchGroup> groups;
    }

    @Setter
    boolean debuggingEnabled;

    boolean initDone = false;
    private Map<String, Pattern> patterns = new HashMap<>();
    List<LogMessageExtract> extracts;

    boolean USE_RE2J = true;
    String NGCHAR = USE_RE2J ? "P" : "";

    @Override
    public void init(List<Rule> rules, RegexService.RegexTokens regexTokens) {

        List<LogMessageExtract> list = new ArrayList<>();
        for (Rule x : rules) {
            if (x instanceof LogMessageExtract) {
                list.add((LogMessageExtract) x);
            }
        }
        this.extracts = list;

        Set<String> idxFields = new HashSet<>();
        for (LogMessageExtract extract : extracts) {
            extract.getExampleTexts().forEach(et -> idxFields.addAll(et.getSelections().keySet()));
        }

        idxFields.forEach(idxField -> {
            StringBuilder finalRegex = new StringBuilder(), debugRegex = new StringBuilder();
            int usefulExtracts = 0;

            for (int iExtract = 0; iExtract < extracts.size(); iExtract++) {
                LogMessageExtract extract = extracts.get(iExtract);
                boolean validRegex = false;
                StringBuilder extractRegex = new StringBuilder(String.format("(?:(?%s<r%d>1)(?:", NGCHAR, iExtract));
                StringBuilder extractDebugRegex = new StringBuilder(String.format("\n(?:\n\t(?%s<r%d>1)(?:", NGCHAR, iExtract));

                for (int iTxt = 0; iTxt < extract.getExampleTexts().size(); iTxt++) {
                    LogMessageExtract.SampleText exampleText = extract.getExampleTexts().get(iTxt);
                    LogMessageExtract.FieldConfig fc = exampleText.getFieldConfigs().get(idxField);

                    if (exampleText.getSelections().containsKey(idxField) && (fc == null || (!fc.isIgnored() && !fc.isPickFullValue()))) {
                        exampleText.getSelections().get(idxField).sort(Comparator.comparingInt(DocField::getStart));
                        StringBuilder tmpRegex = new StringBuilder();
                        int last = 0;
                        String txt = exampleText.getTxt().get(idxField).toString();
                        for (LogMessageExtract.Selection selection : exampleText.getSelections().get(idxField)) {
                            validRegex = true;
                            FieldType type = selection.getType();
                            selection.getRegexTokenNames().forEach(tokName -> {
                                RegexService.RegexToken tok = regexTokens.getRegexFor(tokName);
                                if(tok == null) {
                                    log.warn("{} set for {} not found in regexTokens set", tokName, selection.getName());
                                    return;
                                }
                                if (tok.getProps() != null) {
                                    tok.getProps().forEach(prop -> {
                                        try {
                                            Field f = type.getClass().getDeclaredField(prop.getKey());
                                            f.setAccessible(true);
                                            f.set(type, prop.getValue());
                                        } catch (NoSuchFieldException | IllegalAccessException e) {
                                            // ignore
                                            log.debug("", e);
                                        }
                                    });
                                }
                            });
                            tmpRegex.append(makeEscapedText(txt.substring(last, selection.getStart())));
                            tmpRegex.append(getRegexForSelection(selection, iExtract, iTxt, regexTokens));
                            last = selection.getStart() + selection.getLength();
                        }
                        tmpRegex.append(makeEscapedText(txt.substring(last)));
                        if (iTxt != 0) {
                            extractRegex.append("|");
                            extractDebugRegex.append("\n\t\t|");
                        }
                        extractRegex.append(String.format("(?:(?%s<t%04d%04d>1)%s)", NGCHAR, iExtract, iTxt, tmpRegex));
                        extractDebugRegex.append(String.format("\n\t\t(?:(?%s<t%04d%04d>1)%s)", NGCHAR, iExtract, iTxt, tmpRegex));
                    }
                }

                extractRegex.append("))");
                extractDebugRegex.append("\n\t)\n)");

                if (validRegex) {
                    if (usefulExtracts > 0) {
                        finalRegex.append("|");
                        debugRegex.append("\n|");
                    }
                    finalRegex.append(extractRegex);
                    debugRegex.append(extractDebugRegex);
                    usefulExtracts++;
                }
            }

            if (usefulExtracts != 0) {
                log.info("Regexes for " + idxField);
                String regex = finalRegex.toString();
                log.info("re2 regex =\n" + regex);
                log.info("java regex =\n" + regex.replaceAll("\\?P", "?"));
                log.info("debug regex =\n" + debugRegex);
                patterns.put(idxField, Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE));
            }

        });
        initDone = true;
    }


    @Override
    public boolean eval(Map<String, Object> messageMap, Document d) {
        if (!initDone) {
            throw new IllegalStateException("init is not called on this extractor");
        }

        for (Map.Entry<String, Object> entry : messageMap.entrySet()) {

            String idxField = entry.getKey();
            String text = entry.getValue().toString();

            if (!patterns.containsKey(idxField))
                continue;

            Pattern pattern = patterns.get(idxField);
            Matcher m = pattern.matcher("11" + text);
            if (m.matches()) {

                int matchedRule, matchedSample = 0;
                LogMessageExtract.SampleText extractText = null;

                // find which rule & sample text
                findLoop:
                for (matchedRule = 0; matchedRule < extracts.size(); matchedRule++) {
                    try {
                        if (m.group("r" + matchedRule) != null) {
                            for (matchedSample = 0; matchedSample < extracts.get(matchedRule).getExampleTexts().size(); matchedSample++) {
                                if (m.group(String.format("t%04d%04d", matchedRule, matchedSample)) != null) {
                                    extractText = extracts.get(matchedRule).getExampleTexts().get(matchedSample);
                                    break findLoop;
                                }
                            }
                        }
                    } catch (IllegalArgumentException ignore) {

                    }
                }

                if (extractText == null)
                    continue;

                Map<String, LogMessageExtract.FieldConfig> fc = extractText.getFieldConfigs();

                if (fc.isEmpty() || !fc.get(idxField).isPickFullValue()) {
                    d.put("_matchedRule", extracts.get(matchedRule));
                    List<MatchGroup> grps = new ArrayList<>(extractText.getSelections().size());
                    for (LogMessageExtract.Selection selection : extractText.getSelections().get(idxField)) {
                        if (selection.isIgnore()) continue;
                        try {
                            String key = String.format("%s%04d%04d", makeGrpName(selection.getName()), matchedRule, matchedSample);
                            FieldType type = selection.getType();
                            String val = m.group(key);
                            if (!type.isMultiValued()) {
                                d.put(selection.getName(), type.convert(val));
                            } else {
                                d.putAll((Map<String, Object>) type.convert(val));
                            }
                            if (debuggingEnabled) {
                                int start = m.start(key), end = start + val.length();
                                grps.add(MatchGroup.builder().begin(start).end(end).build());
                            }
                        } catch (Exception e) {
                            log.error("Error while processing selection value " + extracts.get(matchedRule).getName() + "/" + selection.getName(), e);
                            throw new RuntimeException(e);
                        }
                    }

                    extractText.getSelections().forEach((key, selections) -> {
                        if (!fc.isEmpty() && !fc.get(key).isIgnored() && fc.get(key).isPickFullValue())
                            d.put(key, messageMap.get(key));
                    });

                    if (debuggingEnabled) {
                        d.getDebugData().put(
                                "regexMatchBounds",
                                RegexMatch.builder().txt(text).groups(grps).pattern(pattern.pattern()).build()
                        );
                    }
                    return true;
                }
            }
        }
        return false;
    }


    private static String makeEscapedText(String str) {
        str = str
                .replaceAll("[^\\p{Alnum}\n\t\b\r\f'\" ]", "\\\\$0");
        return str
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replaceAll("\\s+", "\\\\s+");
    }

    public static void main(String[] args) {
        System.out.println(makeEscapedText("hi\t  there  how     ae you doing"));
    }

    private String getRegexForSelection(LogMessageExtract.Selection selection, int ruleId, int txtid, RegexService.RegexTokens regexTokens) {
        List<String> ret = new ArrayList<>();
        for (String tokName : selection.getRegexTokenNames()) {
            ret.add(regexTokens.getRegexFor(tokName).getRegex());
        }
        return String.format("(%s%s)",
                selection.isIgnore() ?
                        "" :
                        String.format("?%s<%s%04d%04d>", NGCHAR, makeGrpName(selection.getName()), ruleId, txtid),
                ret.stream().collect(Collectors.joining("|"))
        );
    }

    private String makeGrpName(String name) {
        return name.replaceAll("[^A-Za-z0-9]", "");
    }

}
