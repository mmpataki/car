package car.app.models.reports;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeName(name = "mdiagram", displayName = "MDiagram", description = "Mermaid diagrams")
public class MDiagram extends Visualization {
    String diagramType;
}
