package car.engine.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class SearchResponse {
    String cursor;
    long totalHits;
    List<LogMessage> msgs;
    Map<String, Map<String, Long>> facets;
    Map<String, Map<String, Integer>> rangeFacets;
    Map<String, Map<String, Object>> stats;
    String suggestedView;
}
