package car.app.models.reports;

import car.util.BaseType;
import car.util.TypeName;
import lombok.*;

import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@BaseType
@TypeName(name = "visualization", displayName = "Visualization", description = "visualization")
public abstract class Visualization {

    @Data
    public static class EventHandler {
        String on, type;
        HashMap<String, String> parameters;
    }

    String type;
    String title;
    String description;
    String noDataMessage = "No data available";
    String backgroundColor;
    StoreConfig datastore;
    String preProcessor;

    /* variable selector */
    String ctxtKey, dataKey, defaultValue;

    List<EventHandler> eventHandlers;

    // dictated by frontend lib
    int x, y, w, h, i;

    public Visualization() {}
}
