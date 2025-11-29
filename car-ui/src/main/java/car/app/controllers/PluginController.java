package car.app.controllers;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/plugins")
@CrossOrigin("*")
public class PluginController {

    @GetMapping
    public String[] plugins() {
        File f = new File("./plugins");
        return f.exists() ? f.list() : new String[0];
    }

    @GetMapping("{plugin}")
    public ResponseEntity<InputStreamResource> getPlugin(@PathVariable String plugin) throws FileNotFoundException {
        return ResponseEntity.ok(new InputStreamResource(new FileInputStream(String.format("./plugins/%s/index.js", plugin))));
    }

}
