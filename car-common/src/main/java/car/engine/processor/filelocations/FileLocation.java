package car.engine.processor.filelocations;

import car.util.BaseType;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@BaseType
@AllArgsConstructor
@NoArgsConstructor
@TypeName(name = "filelocation", displayName = "File location", description = "Location of a file (local, http)")
public class FileLocation {
    public String type;
}