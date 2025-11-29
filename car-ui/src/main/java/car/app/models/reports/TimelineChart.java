package car.app.models.reports;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeName(name = "timelinechart", displayName = "Timeline", description = "timeline chart")
public class TimelineChart extends Visualization {
    String fromField;
    String toField;
    String grpByField;
    String labelField;
    int pointWidth = 10;
    String colorField;
    boolean tooltipEnabled = true, yLabelEnabled = true, xLabelEnabled = true;
}
