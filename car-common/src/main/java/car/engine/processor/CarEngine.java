package car.engine.processor;

import car.app.service.RegexService;
import car.common.CarProcessingConfig;
import car.engine.detector.Detector;
import car.engine.models.SearchQuery;
import car.engine.models.SearchResponse;
import car.engine.query.Query;
import car.engine.recordreader.RecordReader;
import car.engine.rules.*;
import car.engine.rules.types.DocField;
import car.engine.rules.types.NumberType;
import car.engine.rules.types.StringType;
import car.engine.store.Document;
import car.engine.store.Store;
import car.engine.store.StoreResponse;
import car.engine.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Slf4j
public class CarEngine extends Engine {

    public CarEngine(EngineOptions opts) {
        super(opts);
    }

    @Override
    public Pair<String, String> detect() throws Exception {

        RuleManager rp = opts.getRuleProvider();
        String file = opts.getFileName(), dsetName = opts.getDsetName();
        Map<Pair<LogTypeGroup, LogType>, Double> confidence = new HashMap<>();

        List<Pair<Detector, Pair<LogTypeGroup, LogType>>> detectors = new ArrayList<>();
        for (String grp : opts.getPossibleLogTypeGroups()) {
            LogTypeGroup logTypeGroup = rp.getLogTypeGroup(grp);
            for (LogType logtype : rp.getLogTypes(logTypeGroup.getName())) {
                for (Detector detector : logtype.getDetectors()) {
                    detectors.add(new Pair<>(detector, new Pair<>(logTypeGroup, logtype)));
                }
            }
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(opts.getDetectStream()));
        String line;
        while ((line = br.readLine()) != null && !detectors.isEmpty()) {
            Iterator<Pair<Detector, Pair<LogTypeGroup, LogType>>> iter = detectors.iterator();
            while (iter.hasNext()) {
                Pair<Detector, Pair<LogTypeGroup, LogType>> detector = iter.next();
                if (detector.getFirst().detect(file, line)) {
                    int total = detector.getSecond().getSecond().getDetectors().size();
                    confidence.compute(detector.getSecond(), (k, v) -> v == null ? (1.0 / total) : v + (1.0 / total));
                    iter.remove();
                }
            }
        }
        log.info("confidence map for {}", file);
        confidence.forEach((lgtlt, aDouble) -> log.info("\t{}/{}: {}", lgtlt.getFirst().getName(), lgtlt.getSecond().getName(), aDouble));
        Optional<Map.Entry<Pair<LogTypeGroup, LogType>, Double>> selectedOp = confidence.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue));
        if (!selectedOp.isPresent() || selectedOp.get().getValue().equals(0.0)) {
            log.info("{} didn't match any log types", file);
            return null;
        } else {
            Pair<LogTypeGroup, LogType> match = selectedOp.get().getKey();
            if (match == null) {
                return null;
            } else {
                log.info("{} is of type {}/{} confidence: [{}]", file, match.getFirst().getName(), match.getSecond().getName(), selectedOp.get().getValue());
                return new Pair(match.getFirst().getName(), match.getSecond().getName());
            }
        }
    }

    @Override
    public void process() throws Exception {

        // setup context
        RuleManager ruleProvider = opts.getRuleProvider();
        String logTypeGrp = opts.getLogTypeGroup(), logTypeName = opts.getLogType(), file = opts.getFileName();
        CarProcessingConfig conf = opts.getConf();
        Store<StoreResponse, Document, Query> store = opts.getStore();
        Store<SearchResponse, Document, SearchQuery> indexStore = opts.getIndexStore();
        if (indexStore == null)
            throw new IllegalArgumentException("indexStore is null");
        if (store == null)
            throw new IllegalArgumentException("store is null");

        if (logTypeGrp == null || logTypeName == null || opts.isDetect()) {
            log.warn("{} not detected, skipping the processing", file);
            return;
        }

        if (!opts.isIndex() && !opts.isIngest())
            return;

        LogType logType = ruleProvider.getLogType(logTypeGrp, logTypeName);
        RecordReader rr = opts.getRecordReader();

        // keep count of rules matched
        Map<String, Long> counts = new HashMap<>();

        // define generated fields
        DocField _msg = DocField.builder().name("_msg").type(new StringType()).ignore(false).build();
        DocField _line = DocField.builder().name("_line").type(new NumberType()).ignore(false).build();
        DocField _file = DocField.builder().name("_file").type(new StringType()).ignore(false).build();
        DocField _mfile = DocField.builder().name("_mfile").type(new StringType()).ignore(false).build();
        DocField _logTyp = DocField.builder().name("_logtyp").type(new StringType()).ignore(false).build();
        DocField _logTypGrp = DocField.builder().name("_logtypgrp").type(new StringType()).ignore(false).build();

        List<Rule> rules = opts.getRuleProvider().getRules(logTypeGrp, logTypeName);
        for (Rule rule : rules) {
            rule.init(rr);
            rule.addField(_line);
            rule.addField(_file);
            rule.addField(_mfile);
            counts.put(rule.getName(), 0l);
        }

        Map<String, List<DocField>> fields = new HashMap<>();
        rules.forEach(rule -> fields.put(String.format("%s_%s", rule.getLogTypeGroup(), rule.getName()), rule.getFields()));
        store.reportFields(fields);

        List<DocField> idxFields = new ArrayList<>(rr.getFields());
        idxFields.add(_msg);
        idxFields.add(_line);
        idxFields.add(_file);
        idxFields.add(_logTyp);
        idxFields.add(_logTypGrp);
        indexStore.reportFields(Collections.singletonMap("", idxFields));

        // setup regex extractor
        RegexExtractor ex = new RegexExtractor();
        ex.init(rules, RegexService.getRegexTokens());

        long i = 1;
        while (rr.hasNext()) {
            Map<String, Object> message = rr.next();
            if (message.isEmpty())
                return;
            try {
                if (opts.isIndex()) {
                    Document doc = new Document("");
                    message.forEach(doc::put);
                    doc.put("_file", file);
                    doc.put("_line", i);
                    doc.put("_logtyp", logTypeName);
                    doc.put("_logtypgrp", logTypeGrp);
                    indexStore.put(doc);
                }
                if (opts.isIngest()) {
                    Document doc = new Document("");
                    if (ex.eval(message, doc)) {
                        Rule rule = (Rule) doc.get("_matchedRule");
                        doc.remove("_matchedRule");
                        doc.put("_file", file);
                        doc.put("_line", i);
                        doc.setType(rule.getLogTypeGroup() + "_" + rule.getName());
                        doc.put("_mfile", logType.indexFileName(file));
                        store.put(doc);
                        counts.compute(rule.getName(), (s, c) -> c == null ? 0 : c + 1);
                    }
                }
                if ((i % 1000) == 0) {
                    log.info("Processed - " + i + " documents");
                    if (conf.getMaxMsgsToProcess() > 0 && i == opts.conf.getMaxMsgsToProcess())
                        break;
                }
            } catch (Exception e) {
                log.error("error while evaluating the line_{}: {}", i, message, e);
            }
            i++;
        }
        log.info("Processed total {} messages", i);
        counts.forEach((key, value) -> log.info(String.format("%5d : %s", value, key)));
    }
}
