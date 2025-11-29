package car.engine.rules;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class LogTypeGroup {

    String name;

    String description;

    transient List<LogType> logTypes = new ArrayList<>();

}
