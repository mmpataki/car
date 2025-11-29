package car.engine.detector;

import car.util.BaseType;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@BaseType
@TypeName(name = "detector", displayName = "Detector", description = "File type detectors")
public abstract class Detector {

    String type;

    public abstract boolean detect(String fileName, String line) throws Exception;

    public void init() {

    }

}
