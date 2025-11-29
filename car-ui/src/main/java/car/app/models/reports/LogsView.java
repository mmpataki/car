package car.app.models.reports;

import car.engine.search.SearchView;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeName(name = "logmessages", displayName = "LogView", description = "Log messages")
public class LogsView extends Visualization {
    List<String> displayedFields;
    SearchView customView;
    String viewName;
    String colorField;
}
