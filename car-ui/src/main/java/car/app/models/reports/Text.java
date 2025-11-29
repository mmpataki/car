package car.app.models.reports;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeName(name = "text", displayName = "Text", description = "Text")
public class Text extends Visualization {
    String fontSize;
    boolean monospace;
}
