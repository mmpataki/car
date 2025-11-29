package car.engine.recordreader.rulebasedreader;

import car.engine.recordreader.ReadConfig;
import car.engine.recordreader.StructuredRecordReader;
import car.engine.rules.Rule;
import car.util.TypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@TypeName(name = "rulebased", displayName = "rule based reader", description = "")
public class RuleBasedReaderConfig extends ReadConfig {

    List<Rule> readRules = new ArrayList<>();

    @Override
    public StructuredRecordReader buildReader() {
        return new RuleBasedStructureReader(readRules);
    }
}
