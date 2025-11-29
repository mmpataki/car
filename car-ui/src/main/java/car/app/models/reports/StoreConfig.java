package car.app.models.reports;

import car.util.BaseType;
import car.util.TypeName;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@BaseType
@TypeName(name = "storeconfig", displayName = "Store config", description = "store config")
public class StoreConfig {
    String type;
}
