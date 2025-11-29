package car.engine.recordreader.unstructured;

import car.app.service.RegexService;
import car.engine.recordreader.RecordReader;
import car.engine.rules.types.DocField;
import car.engine.rules.types.StringType;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static car.engine.util.Utils.wrapLine;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeName(name = "singlelinerecordreader", displayName = "Single line log message reader", description = "reads the record line by line")
public class LineRecordReader extends RecordReader {

    String line;
    private BufferedReader br;

    @Override
    public void init(RegexService.RegexTokens toks) {
        br = new BufferedReader(new InputStreamReader(getInputStream()));
        advance();
    }

    @Override
    public List<String> getFieldNames() {
        return Collections.singletonList("_msg");
    }

    @Override
    public List<DocField> getFields() {
        return Collections.singletonList(DocField.builder().name("_msg").type(new StringType()).build());
    }

    @Override
    public boolean _hasNext() {
        return (line != null);
    }

    @Override
    public Map<String, Object> _next() {
        String ret = line;
        advance();
        return wrapLine(ret);
    }

    private void advance() {
        try {
            line = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
