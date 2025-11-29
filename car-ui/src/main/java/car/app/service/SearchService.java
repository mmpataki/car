package car.app.service;

import car.engine.search.SearchView;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SearchService {

    String BASE_PATH = "./search-views";
    Gson gson = new Gson();

    public SearchService() {
        if (!new File(BASE_PATH).exists()) {
            new File(BASE_PATH).mkdir();
        }
    }

    public SearchView getSearchView(String name) throws Exception {
        try (FileReader fr = new FileReader(BASE_PATH + File.separator + name + ".json")) {
            return gson.fromJson(fr, SearchView.class);
        }
    }

    public List<SearchView> getSearchViews() throws IOException {
        File dir = new File(BASE_PATH);
        List<SearchView> ret = new ArrayList<>();

        if (!dir.exists())
            return Collections.emptyList();
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                try (FileReader fr = new FileReader(file)) {
                    ret.add(gson.fromJson(fr, SearchView.class));
                }
            }
        }
        return ret;
    }

    public void addSearchView(SearchView sv) throws IOException {
        assert sv.getName() != null && !sv.getName().isEmpty() : "Name cannot be empty";
        try (FileWriter fw = new FileWriter(BASE_PATH + "/" + sv.getName() + ".json")) {
            gson.toJson(sv, fw);
        }
    }
}
