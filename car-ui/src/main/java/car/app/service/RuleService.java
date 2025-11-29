package car.app.service;

import car.app.CarAppConfiguration;
import car.app.service.api.AuthorizationService;
import car.engine.rules.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class RuleService {

    @Autowired
    CarAppConfiguration conf;

    @Autowired
    AuthorizationService AS;

    RuleManager RM = new FileBasedRuleManager();

    public RuleService() throws IOException {
    }

    public class RuleSyncer implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    RuleService.this.sync();
                } catch (Exception e) {
                    log.error("Error while syncing content", e);
                }
                try {
                    Thread.sleep(1000 * 60 * 5);
                } catch (Exception e) {
                    log.error("Sleep interrupted", e);
                }
            }
        }
    }

    public class ContentBundler implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    RuleService.this.bundle();
                } catch (Exception e) {
                    log.error("Error while bundling content", e);
                }
                try {
                    Thread.sleep(1000 * 60);
                } catch (Exception e) {
                    log.error("Sleep interrupted", e);
                }
            }
        }
    }

    public static void pack(String zipFilePath, String... sourceDirPaths) throws IOException {
        Path p = Paths.get(zipFilePath);
        if (!new File(zipFilePath).exists()) {
            p = Files.createFile(Paths.get(zipFilePath));
        }
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            for (String sourceDirPath : sourceDirPaths) {
                Path pp = Paths.get(sourceDirPath);
                Files.walk(pp, FileVisitOption.FOLLOW_LINKS)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            ZipEntry zipEntry = new ZipEntry(sourceDirPath + "/" + pp.relativize(path).toString().replaceAll("\\\\", "/"));
                            try {
                                zs.putNextEntry(zipEntry);
                                Files.copy(path, zs);
                                zs.closeEntry();
                            } catch (IOException e) {
                                System.err.println(e);
                            }
                        });
            }
        }
    }

    protected void bundle() throws IOException {
        log.info("Bundling rules, dashboards");
        pack("rd.zip", "rules", "dashboards");
        log.info("Bundling complete");
    }


    public void sync() throws Exception {
        /* just download and unzip it here, its fast and foolproof */
        log.info("Syncing rules from master @ " + conf.getDeploymentMasterUrl());
        URL oracle = new URL(conf.getDeploymentMasterUrl() + "/rd.zip");
        BufferedInputStream in = new BufferedInputStream(oracle.openStream());
        extractZip(in);
    }

    private String fileName(String root, ArchiveEntry e) {
        return root + File.separator + e.getName();
    }

    public void extractZip(InputStream is) throws Exception {
        try (ArchiveInputStream i = new ArchiveStreamFactory()
                .createArchiveInputStream(is)) {
            ArchiveEntry entry = null;
            while ((entry = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(entry)) {
                    // log something?
                    continue;
                }
                String name = fileName(".", entry);
                File f = new File(name);
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) {
                        throw new IOException("failed to create directory " + f);
                    }
                } else {
                    File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("failed to create directory " + parent);
                    }
                    try (OutputStream o = Files.newOutputStream(f.toPath())) {
                        IOUtils.copy(i, o);
                    }
                }
            }
        }
    }


    @PostConstruct
    public void init() {
        boolean isMaster = conf.isMaster();
        if (isMaster)
            new Thread(new ContentBundler(), "Rule-bundling-thread").start();
        else
            new Thread(new RuleSyncer(), "Sync-Content-Thread").start();
    }

    public LogTypeGroup getLogTypeGroup(String name) {
        return RM.getLogTypeGroup(name);
    }

    public List<LogTypeGroup> getLogTypeGroups() {
        return RM.getLogTypeGroups();
    }

    public List<LogType> getLogTypes(String group) {
        return RM.getLogTypes(group);
    }

    public LogType getLogType(String group, String logtype) {
        return RM.getLogType(group, logtype);
    }

    public void registerLogTypeGroup(LogTypeGroup grp) throws IOException {
        RM.registerLogTypeGroup(grp);
    }

    public void addLogType(String group, LogType type) throws IOException {
        RM.addLogType(group, type);
    }

    public void addRule(String group, String type, Rule rule) throws Exception {
        AS.checkRuleCreatePermission();
        _addRule(group, type, rule);
    }

    public void _addRule(String group, String type, Rule rule) throws IOException {
        RM._addRule(group, type, rule);
    }

    public void deleteRule(String group, String type, String rule) throws Exception {
        AS.checkRuleDeletePermission();
        _deleteRule(group, type, rule);
    }

    public void _deleteRule(String group, String type, String rule) throws IOException {
        RM._deleteRule(group, type, rule);
    }

    public List<Rule> getRules(String group, String type) {
        return RM.getRules(group, type);
    }
}
