package car.engine.recordreader.unstructured;

import car.app.service.RegexService;
import car.engine.recordreader.RecordReader;
import car.engine.rules.types.DocField;
import car.engine.rules.types.StringType;
import car.util.FieldDesc;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static car.engine.util.Utils.wrapLine;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeName(name = "multilinerecordreader", displayName = "Multi line record reader", description = "reads multi line log msgs")
public class MultiLineLogMsgReader extends RecordReader {

    @FieldDesc(description = "regex to match first line", displayName = "First line regex")
    String msgStartRegex;

    transient String line, lastLine = null;
    transient private BufferedReader br;
    transient Pattern lineStartRegex;

    @Override
    public void init(RegexService.RegexTokens toks) {
        br = new BufferedReader(new InputStreamReader(getInputStream()));
        lineStartRegex = Pattern.compile(msgStartRegex);
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
            StringBuilder sb = new StringBuilder(lastLine == null ? "" : lastLine);
            boolean first = lastLine == null;
            while (true) {
                lastLine = br.readLine();
                if (!first && lastLine != null && lineStartRegex.matcher(lastLine).matches())
                    break;
                first = false;
                if (lastLine != null)
                    sb.append(lastLine).append("\n");
                else
                    break;
            }
            line = sb.toString().isEmpty() ? null : sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
