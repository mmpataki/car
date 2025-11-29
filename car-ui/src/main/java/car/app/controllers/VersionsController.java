package car.app.controllers;

import car.app.service.api.VersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VersionsController {

    @Autowired
    VersionControlService VCS;

}
