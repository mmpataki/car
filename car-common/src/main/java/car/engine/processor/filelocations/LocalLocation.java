package car.engine.processor.filelocations;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@TypeName(name = "locallocation", displayName = "locallocation", description = "locallocation")
public class LocalLocation extends FileLocation {
    String path;
    public static LocalLocation makeLocalLocation(String path) {
        LocalLocation lp = LocalLocation.builder().path(path).build();
        lp.setType("locallocation");
        return lp;
    }
}
