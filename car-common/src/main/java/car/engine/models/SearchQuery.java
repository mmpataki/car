package car.engine.models;

import car.engine.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchQuery {

    @Data
    public class RangeFacetQuery {
        Number start, end, gap;
        String field, type;
    }

    Map<String, String> query;
    int pageSize;
    String cursor;
    List<Pair<String, String>> sortFields;
    List<String> fields;
    boolean facet;
    List<String> facetFields;
    int facetCount;
    boolean suggestView;

    List<RangeFacetQuery> rangeFacets;
    List<String> statFields;
}