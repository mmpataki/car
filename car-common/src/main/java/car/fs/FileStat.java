package car.fs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileStat {
    String path;
    long length;
    long createdDate;
}