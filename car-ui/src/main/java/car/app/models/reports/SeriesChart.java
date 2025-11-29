package car.app.models.reports;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeName(name = "serieschart", displayName = "Series Charts (Bar / Area / Line)", description = "Bar / Line / Area charts")
public class SeriesChart extends Visualization {
    String chartType, xfield;
    boolean showLegend, xIsDateTime = true, stackValues = false;
    int lineWidth;
    String groupBy, labelField;
    Object yfields;
    boolean yAxisLogarithmic;

    // this should be nullable
    Integer yAxisTickInterval;
}
