package car.app.service;

import car.app.beans.GsonConfigurer;
import car.app.models.reports.Dashboard;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DashboardService {

    public String rootLocation = "./dashboards";

    public void saveDashboard(Dashboard dboard) throws IOException {
        Files.createDirectories(Paths.get(getDashBoardDir(dboard.getLogTypeGroup(), dboard.getLogType())));
        try(FileWriter fw = new FileWriter(getDashBoardPath(dboard.getLogTypeGroup(), dboard.getLogType(), dboard.getName()))) {
            new Gson().toJson(dboard, fw);
        }
    }

    private String getDashBoardDir(String grp, String typ) {
        return String.format("%s/%s/%s/", rootLocation, grp == null ? "" : grp, typ == null ? "" : typ);
    }

    private String getDashBoardPath(String grp, String typ, String name) {
        return String.format("%s/%s.json", getDashBoardDir(grp, typ), name);
    }

    public List<Dashboard> getDashBoardsFor(String grp, String typ) throws IOException {
        Gson G = GsonConfigurer.gson();
        try {
            return Files.list(Paths.get(getDashBoardDir(grp, typ))).map(path -> {
                try (FileReader fr = new FileReader(path.toFile())) {
                    return G.fromJson(fr, Dashboard.class);
                } catch (IOException e) {
                    log.error("couldn't read the dashboard file: " + path, e);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
