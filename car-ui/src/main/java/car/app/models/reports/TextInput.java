package car.app.models.reports;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeName(name = "textinput", displayName = "Text input", description = "Text input")
public class TextInput extends Visualization {
    String btnText;
    String label;
    boolean showSubmitBtn;
}
