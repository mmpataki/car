package car.engine.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchView {

    @Data
    public static class SearchField {
        String name;
        String decodeAs;
        String decodeFunc;
        String align;
        boolean wrap, visible, queried;
    }

    public String name;
    public List<SearchField> fields;

}
