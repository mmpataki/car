package car.app.controllers;

import car.app.service.api.StorageService;
import car.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    StorageService SR;

    @PostMapping("/file")
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file) throws Exception {
        return SR.store(SecurityUtil.getCurrentUser(), file);
    }


}
