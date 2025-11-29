package car.engine.recordreader;

import car.app.service.RegexService;
import car.engine.processor.TrackingInputStream;
import car.engine.rules.types.DocField;
import car.util.BaseType;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@BaseType
@TypeName(name = "recordreader", displayName = "record reader", description = "type of records present in this log file")
public abstract class RecordReader implements Iterator<Map<String, Object>> {

    String type;
    long limit = Long.MAX_VALUE, lastRecordStart = 0;
    String fileName;
    TrackingInputStream inputStream;

    public void setInputStream(TrackingInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public boolean hasNext() {
        if ((lastRecordStart = inputStream.getBytesRead()) > limit)
            return false;
        return _hasNext();
    }

    @Override
    public Map<String, Object> next() {
        while (true) {
            try {
                return _next();
            } catch (Exception e) {
                log.error("Error while reading next record, problematic range of bytes {} - {}", lastRecordStart, inputStream.getBytesRead(), e);
                if(!hasNext())
                    return Collections.emptyMap();
            }
        }
    }

    public abstract Map<String, Object> _next();

    public abstract boolean _hasNext();

    public abstract void init(RegexService.RegexTokens tokens) throws Exception;

    public abstract List<String> getFieldNames();

    public abstract List<DocField> getFields();

}
