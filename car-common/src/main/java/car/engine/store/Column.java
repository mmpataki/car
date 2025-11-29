package car.engine.store;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Column {
    String name, type;
}
