package car.engine.rules;

import car.engine.processor.Dataset;
import car.engine.recordreader.RecordReader;
import car.engine.recordreader.StructuredRecordReader;
import car.engine.rules.types.DocField;
import car.engine.store.Document;
import car.util.FieldDesc;
import car.util.TypeName;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@TypeName(name = "regexv2", displayName = "Regex V2", description = "Java regex to extract msgs")
@Slf4j
@ToString
public class LogMessageExtract extends Rule {

    @Data
    static class Selection extends DocField {
    }

    @Data
    public class FieldConfig {
        boolean ignored, pickFullValue;
    }

    @Data
    public static class SampleText {
        Map<String, Object> txt = new HashMap<>();
        Map<String, FieldConfig> fieldConfigs = new HashMap<>();
        Map<String, List<Selection>> selections = new HashMap<>();
        Map<String, List<Selection>> inheritedFields = new HashMap<>();
    }

    @FieldDesc(displayName = "example tests", description = "example log msgs which match this patters")
    List<SampleText> exampleTexts;

    /* dynamic selections */
    transient List<DocField> extraFields;

    @Override
    public void init(RecordReader rr) {
        exploreFields(rr);
    }

    @Override
    protected void _addField(DocField f) {
        if (extraFields == null)
            extraFields = new LinkedList<>();
        extraFields.add(f);
    }

    @Override
    public Collection<DocField> _getFields() {
        List<DocField> ret = Stream.concat(
                exampleTexts.stream().flatMap(et -> et.getSelections().values().stream().flatMap(Collection::stream).filter(f -> !f.isIgnore())),
                exampleTexts.stream().flatMap(et -> et.getInheritedFields().values().stream().flatMap(Collection::stream))
        ).collect(Collectors.toList());
        if (extraFields != null)
            ret.addAll(extraFields);
        return ret;
    }

    @Override
    public boolean eval(String message, Document doc, Dataset dset) {
        return false;
    }

    public void exploreFields(RecordReader reader) {
        Map<String, DocField> inheritedFields = new HashMap<>();
        List<DocField> flds = new LinkedList<>();
        if (reader instanceof StructuredRecordReader) {
            StructuredRecordReader srr = (StructuredRecordReader) reader;
            for (DocField field : srr.getFields()) {
                inheritedFields.put(field.getName(), field);
            }
            for (SampleText et : getExampleTexts()) {
                et.getFieldConfigs().forEach((fName, fieldConfig) -> {
                    if (!fieldConfig.isIgnored() && fieldConfig.isPickFullValue()) {
                        if (!inheritedFields.containsKey(fName)) {
                            throw new IllegalStateException("Looks like rule " + getName() + " is corrupt, it's inheriting non-existent field from record reader");
                        }
                        Selection sel = new Selection();
                        sel.setName(fName);
                        sel.setType(inheritedFields.get(fName).getType());
                        Map<String, List<Selection>> m = et.getInheritedFields();
                        if (m == null)
                            et.setInheritedFields(m = new HashMap<>());
                        m.computeIfAbsent(fName, s -> new LinkedList<>()).add(sel);
                    }
                });
            }
        }
    }

}
