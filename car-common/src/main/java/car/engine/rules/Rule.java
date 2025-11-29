package car.engine.rules;

import car.engine.processor.Dataset;
import car.engine.recordreader.RecordReader;
import car.engine.rules.types.DocField;
import car.engine.store.Document;
import car.util.BaseType;
import car.util.TypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@BaseType
@TypeName(name = "rule", displayName = "Rule", description = "A rule to extract info from a log message")
public abstract class Rule {

    /* needed to decode subtypes */
    public String type;

    public String name;

    public String description;

    transient public String logTypeGroup, logType;

    // rules can check the fields in the reader and initialize themselves
    public abstract void init(RecordReader rr);

    public void addField(DocField f) {
        _addField(f);
    }

    protected abstract void _addField(DocField f);

    public List<DocField> getFields() {
        ArrayList<DocField> fields = new ArrayList<>();
        for (DocField field : _getFields()) {
            if(field.getType().isMultiValued()) {
                fields.addAll(field.getType().getSubFields());
            } else {
                fields.add(field);
            }
        }
        return fields;
    }

    public abstract Collection<DocField> _getFields();

    public abstract boolean eval(String message, Document doc, Dataset dset);

}
