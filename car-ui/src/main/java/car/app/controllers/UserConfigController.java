package car.app.controllers;

import car.app.service.DatasetService;
import car.app.service.UserConfigService;
import car.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/user-config")
@CrossOrigin("*")
public class UserConfigController {

    @Autowired
    UserConfigService US;

    @GetMapping("pinned")
    public List<DatasetService.DatasetSummary> getPinned() throws IOException {
        return US.getPinnedDatasets();
    }

    @DeleteMapping("{id}/unpin")
    public void removePinned(@PathVariable String id) throws Exception {
        US.removePinnedDatasets(Collections.singletonList(id));
    }

    @PostMapping("{id}/pin")
    public void pin(@PathVariable String id) throws Exception {
        US.savePinned(Collections.singletonList(id));
    }

    @PostMapping("{id}/pin/for/{user}")
    public void pinFor(@PathVariable String id, @PathVariable String user) throws Exception {
        SecurityUtil.setCurrentUser(user);
        pin(id);
    }
}
