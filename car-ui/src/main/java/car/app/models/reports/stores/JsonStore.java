package car.app.models.reports.stores;

import car.app.models.reports.StoreConfig;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeName(name = "json", displayName = "JSON store", description = "store which reads a JSOn array and produces output")
public class JsonStore extends StoreConfig {

    String json;

}
