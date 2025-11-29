package car.app.models.reports;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@TypeName(name = "visgenerator", displayName = "Visualization loop", description = "Loops through results and draws a visulazation for each of them")
public class VisulazationLooper extends Visualization {

    Boolean isGenerator;
    String flowType, subtype;
    Visualization childvisualization;

}
