package car.app.controllers;

import car.app.service.SearchService;
import car.engine.search.SearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    SearchService SS;

    @GetMapping("search-views")
    public List<SearchView> getSearchViews() throws Exception {
        return SS.getSearchViews();
    }

    @PostMapping("search-views")
    public void addSearchViews(@RequestBody SearchView sv) throws IOException {
        SS.addSearchView(sv);
    }

}
