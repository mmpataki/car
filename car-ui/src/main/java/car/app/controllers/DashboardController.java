package car.app.controllers;

import car.app.service.DashboardService;
import car.app.models.reports.Dashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/dashboards")
public class DashboardController {

    @Autowired
    DashboardService DS;

    @PostMapping
    public void publishDashBoard(@RequestBody Dashboard db) throws IOException {
        DS.saveDashboard(db);
    }

    @GetMapping
    public List<Dashboard> getDashboardsWith(@RequestParam("group") String group, @RequestParam("type") String type) throws IOException {
        return DS.getDashBoardsFor(group, type);
    }

}
