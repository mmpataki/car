package car.engine.rules.types;

import car.util.TypeName;
import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.TimeZone;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@TypeName(name = "dateftype", displayName = "date", description = "date")
public class DateType extends FieldType<Long> {
    String format;
    transient int offset = 0;
    transient SimpleDateFormat sdf;

    @Override
    public Long convert(String v) throws ParseException {
        if (sdf == null) {
            sdf = new SimpleDateFormat(format);
            String tzOffset = System.getProperty("timeZoneOffset");
            if (tzOffset != null) {
                ZoneOffset zoff = ZoneOffset.of(tzOffset);
                sdf.setTimeZone(TimeZone.getTimeZone(zoff));
                offset = zoff.getTotalSeconds() * 1000;
            }
        }
        return sdf.parse(v).getTime() + offset;
    }
}
