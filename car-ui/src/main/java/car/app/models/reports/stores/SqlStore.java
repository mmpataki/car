package car.app.models.reports.stores;

import car.app.models.reports.StoreConfig;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeName(name = "sql", displayName = "SQL Store", description = "SQL store")
public class SqlStore extends StoreConfig {
    String sql;
}
