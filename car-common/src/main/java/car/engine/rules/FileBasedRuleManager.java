package car.engine.rules;

import car.util.GsonMaker;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class FileBasedRuleManager implements RuleManager {

    Gson G = GsonMaker.getGson();
    final String
            RULES_DIR = "./rules",
            GROUP_FILE_NAME = "grp.info",
            TYPE_FILE_NAME = "type.info";

    public FileBasedRuleManager() throws IOException {
        if (!(new File(RULES_DIR)).exists())
            Files.createDirectory(Paths.get("rules")).toFile();
    }

    public List<LogTypeGroup> getLogTypeGroups() {
        return Arrays.asList(new File(RULES_DIR).listFiles()).stream()
                .filter(f -> f.isDirectory())
                .map(f -> f.getName())
                .map(grpName -> getLogTypeGroup(grpName))
                .filter(o -> o != null)
                .collect(Collectors.toList());
    }

    public LogTypeGroup getLogTypeGroup(String grpName) {
        File fp = new File(RULES_DIR + File.separator + grpName + File.separator + GROUP_FILE_NAME);
        try {
            return G.fromJson(new FileReader(fp), LogTypeGroup.class);
        } catch (FileNotFoundException e) {
            log.error("error while reading: " + fp, e);
            return null;
        }
    }

    public void registerLogTypeGroup(LogTypeGroup grp) throws IOException {
        File grpdir = new File(RULES_DIR + File.separator + grp.getName());
        if (!grpdir.exists())
            grpdir.mkdirs();
        writeJson(grp, grpdir.getAbsolutePath() + File.separator + GROUP_FILE_NAME);
    }

    private void writeJson(Object grp, String fp) throws IOException {
        FileWriter fw = new FileWriter(fp);
        G.toJson(grp, fw);
        fw.close();
    }


    public List<LogType> getLogTypes(String group) {
        if (!new File(RULES_DIR + File.separator + group).exists())
            return Collections.emptyList();
        return Arrays.asList(new File(RULES_DIR + File.separator + group).list()).stream()
                .map(typ -> new File(RULES_DIR + File.separator + group + File.separator + typ + File.separator + TYPE_FILE_NAME))
                .filter(f -> f.exists())
                .map(f -> {
                    try (FileReader fr = new FileReader(f)) {
                        return G.fromJson(fr, LogType.class);
                    } catch (IOException e) {
                        log.error("couldn't read " + f.getAbsolutePath(), e);
                        return null;
                    }
                })
                .filter(o -> o != null)
                .collect(Collectors.toList());
    }

    public LogType getLogType(String group, String logtype) {
        String path = RULES_DIR + File.separator + group + File.separator + logtype + File.separator + TYPE_FILE_NAME;
        try {
            return G.fromJson(new FileReader(path), LogType.class);
        } catch (FileNotFoundException e) {
            log.error("couldn't read " + path, e);
            return null;
        }
    }

    public void addLogType(String group, LogType type) throws IOException {
        File typd = new File(RULES_DIR + File.separator + group + File.separator + type.getName());
        if (!typd.exists())
            typd.mkdirs();
        writeJson(type, RULES_DIR + File.separator + group + File.separator + type.getName() + File.separator + TYPE_FILE_NAME);
    }

    public void _addRule(String group, String type, Rule rule) throws IOException {
        writeJson(rule, RULES_DIR + File.separator + group + File.separator + type + File.separator + rule.getName());
    }

    public void _deleteRule(String group, String type, String rule) throws IOException {
        String path = makeRulePath(group, type, rule);
        new File(path).delete();
    }

    public List<Rule> _getRules(String group, String type) {
        return Arrays.asList(Objects.requireNonNull(new File(makeRulePath(group, type)).listFiles())).stream()
                .filter(f -> !f.getName().equals(TYPE_FILE_NAME))
                .map(f -> {
                    try {
                        return G.fromJson(new FileReader(f), Rule.class);
                    } catch (FileNotFoundException e) {
                        log.debug("error while reading " + f.getAbsolutePath(), e);
                        return null;
                    }
                })
                .filter(o -> o != null)
                .collect(Collectors.toList());
    }

    private String makeRulePath(String... chunks) {
        return RULES_DIR + File.separator + Arrays.stream(chunks).collect(Collectors.joining("/"));
    }
}

