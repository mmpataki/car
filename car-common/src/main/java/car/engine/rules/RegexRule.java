package car.engine.rules;

import car.engine.processor.Dataset;
import car.engine.recordreader.RecordReader;
import car.engine.rules.types.DocField;
import car.engine.rules.types.FieldType;
import car.engine.store.Document;
import car.util.FieldDesc;
import car.util.TypeName;
import lombok.*;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Builder
@Getter
@Setter
@TypeName(name = "regex", displayName = "Regex", description = "Java regex to extract msgs")
@Slf4j
@ToString
@NoArgsConstructor
public class RegexRule extends Rule {

    @FieldDesc(displayName = "pattern", description = "A java regex pattern")
    String pattern;

    @FieldDesc(displayName = "group names map", description = "A mapping from group-number to names given to them")
    Map<Integer, DocField> groupNameMap;

    /* dynamic selections */
    transient List<DocField> extraFields = new ArrayList<>();

    @FieldDesc(displayName = "example tests", description = "example log msgs which match this patters")
    List<String> exampleTexts;

    transient Pattern p;

    Boolean manualRegex = false;

    @Override
    public void init(RecordReader rr) {
        p = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.DOTALL);
    }

    @Override
    protected void _addField(DocField f) {
        if (extraFields == null)
            extraFields = new LinkedList<>();
        extraFields.add(f);
    }

    @Override
    public Collection<DocField> _getFields() {
        ArrayList<DocField> ret = new ArrayList<>(groupNameMap.values());
        if (extraFields != null)
            ret.addAll(extraFields);
        return ret;
    }

    @Override
    public boolean eval(String message, Document d, Dataset dset) {
        Matcher m = p.matcher(message);
        if (m.matches()) {
            for (Map.Entry<Integer, DocField> grpname : groupNameMap.entrySet()) {
                if (grpname.getKey() <= m.groupCount()) {
                    try {
                        FieldType type = grpname.getValue().getType();
                        String str = m.group(grpname.getKey());
                        if (!type.isMultiValued()) {
                            d.put(
                                    grpname.getValue().getName(),
                                    type.convert(str)
                            );
                        } else {
                            d.putAll((Map<String, Object>) type.convert(str));
                        }
                    } catch (ParseException e) {
                        log.warn(String.format("Couldn't parse line, skipping:\n\t %s", message), e);
                    }
                }
            }
            return true;
        }
        return false;
    }

}
