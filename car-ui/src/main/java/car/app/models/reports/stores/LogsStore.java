package car.app.models.reports.stores;

import car.app.models.reports.StoreConfig;
import car.engine.util.Pair;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeName(name = "logs", displayName = "Logs Store", description = "Log messages store")
public class LogsStore extends StoreConfig {
    String query;       // a json query
    int pageSize;
    String mode;        // facet || logs
    List<String> queriedFields;
    List<String> facetFields;
    String sortField, sortOrder;
    List<Pair<String, String>> sortFields;
}
