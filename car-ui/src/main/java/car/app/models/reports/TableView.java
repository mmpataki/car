package car.app.models.reports;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeName(name = "table", displayName = "Table", description = "Table")
public class TableView extends Visualization {
    boolean keyValueDisplay;
}
