package car.app.models.reports;

import car.util.GsonMaker;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Dashboard {

    String name;
    String description;

    /* when these are not set, these can be global */
    String logTypeGroup, logType;

    List<Visualization> visualizations = new ArrayList<>();

    String background = "#eceff1", visBackground = "white", visTitleColor = "#2f63a0";
    String textTransform = "";
    int titleFontSize = 12;

    @Data
    public static class Variable {
        String name;
        String type; // number / string / json
        String defaultValue;
    }

    List<Variable> variables = new ArrayList<>();


}
