package car.app.controllers;

import car.app.service.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    AdminService AS;

    @Autowired
    UserGroupService UGS;

    @PostMapping("/backup")
    public void backup() throws Exception {
        if(!UGS.amIAdmin()) {
            throw new UnAuthorizedException("You are not authorized to do this action");
        }
        AS.backup();
    }

    @PostMapping("/restore")
    public void restore() throws Exception {
        if(!UGS.amIAdmin()) {
            throw new UnAuthorizedException("You are not authorized to do this action");
        }
        AS.restore();
    }

}
