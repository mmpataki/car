package car.engine.detector;

import car.util.FieldDesc;
import car.util.TypeName;
import lombok.*;

import java.io.InputStream;
import java.util.regex.Pattern;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@TypeName(name = "filenamematching", displayName = "Filename based detector", description = "A detector detecting log types based on file names")
public class FileNameBasedDetector extends Detector {

    @FieldDesc(displayName = "pattern", description = "Pattern to match the file name against")
    String pattern;

    @Override
    public boolean detect(String fileName, String line) throws Exception {
        return Pattern.matches(pattern, fileName);
    }
}
