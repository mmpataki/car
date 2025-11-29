package car.engine.detector;

import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeName(name = "messagefinding", displayName = "message finder", description = "classifies a document based on few msgs existance")
public class
MessageFinder extends Detector {

    public enum MessageType {
        Regex_matching, Text_search;
    }

    @Data
    public static class Message {
        String text;
        MessageType type;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Message message = (Message) o;

            if (!text.equals(message.text)) return false;
            return type == message.type;
        }

        @Override
        public int hashCode() {
            int result = text.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }
    }

    Set<Message> msgs;

    boolean init;
    List<Message> texts;
    List<Pattern> regexes;

    @Override
    public boolean detect(String fileName, String line) throws Exception {
        if(!init) {
            texts = msgs.stream().filter(m -> m.type == MessageType.Text_search).collect(Collectors.toList());
            regexes = msgs.stream().filter(m -> m.type == MessageType.Regex_matching).map(m -> Pattern.compile(m.text)).collect(Collectors.toList());
            init = true;
        }
        return (texts.stream().anyMatch(m -> line.contains(m.text))) || regexes.stream().anyMatch(m -> m.matcher(line).matches());
    }
}
