package car.engine.query;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Query {
    Op criteria;
    String query;
}
