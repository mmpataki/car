package car.engine.store;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StoreResponse {
    String[] fieldNames;
    List<Object[]> data;
}
