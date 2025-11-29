package car.app.models.reports;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeName(name = "dropdown", displayName = "Dropdown", description = "Dropdown for selecting variable values")
public class Dropdown extends Visualization {
    String labelKey;
    String label;
}
