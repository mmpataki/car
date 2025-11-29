package car.engine.processor.filelocations;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@TypeName(name = "httplocation", displayName = "httplocation", description = "httplocation")
public class HttpLocation extends FileLocation {
    String url;
    String user, password;
    public static HttpLocation makeHttpLocation(String url, String user, String password) {
        HttpLocation hp = HttpLocation.builder().url(url).user(user).password(password).build();
        hp.setType("httplocation");
        return hp;
    }
}
