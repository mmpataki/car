package car.app.models.reports;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeName(name = "tree", displayName = "Tree view", description = "Tree view")
public class TreeView extends Visualization{

    String pathField;
    String separator;
    String labelField;
    String urlField;

}
