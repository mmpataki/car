package car.engine.recordreader.csvreader;

import car.app.service.RegexService;
import car.engine.processor.Dataset;
import car.engine.processor.TrackingInputStream;
import car.engine.recordreader.StructuredRecordReader;
import car.engine.rules.types.DocField;
import car.engine.rules.types.FieldType;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CsvReader extends StructuredRecordReader {

    CsvReadConfig conf;
    CSVReader reader;
    String row[];
    Dataset ds = new Dataset();

    DocField[] mappers;

    public CsvReader(CsvReadConfig config) {
        this.conf = config;
    }

    @Override
    public void setInputStream(TrackingInputStream inputStream) {
        super.setInputStream(inputStream);
        reader = new CSVReaderBuilder(new InputStreamReader(getInputStream())).withCSVParser(
                new RFC4180Parser()
        ).build();
    }

    @Override
    public void init(RegexService.RegexTokens regexTokens) throws Exception {

        setInputStream(getInputStream());

        if (conf.isFirstRowHeader()) {
            String[] headers = reader.readNext();
            log.info("CSV Headers: " + Arrays.toString(headers));

            mappers = new DocField[headers.length];
            for (int i = 0; i < headers.length; i++) {
                mappers[i] = conf.getFieldMappings().get(headers[i]);
            }
        } else {
            mappers = new DocField[conf.getFieldMappings().size()];
            for (int i = 0; i < mappers.length; i++) {
                mappers[i] = conf.getFieldMappings().get("col-" + i);
            }
        }

        for (DocField selection : mappers) {
            if (selection == null)
                continue;
            FieldType type = selection.getType();
            selection.getRegexTokenNames().forEach(tokName -> {
                RegexService.RegexToken tok = regexTokens.getRegexFor(tokName);
                if (tok.getProps() != null) {
                    tok.getProps().forEach(prop -> {
                        try {
                            Field f = type.getClass().getDeclaredField(prop.getKey());
                            f.setAccessible(true);
                            f.set(type, prop.getValue());
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            // ignore
                            log.debug("", e);
                        }
                    });
                }
            });
        }
    }

    @SneakyThrows
    @Override
    public boolean _hasNext() {
        row = reader.readNextSilently();
        return row != null;
    }

    @SneakyThrows
    @Override
    public Map<String, Object> _next() {
        Map<String, Object> ret = new HashMap<>();
        for (int i = 0; i < row.length; i++) {
            try {
                if (mappers[i] == null || mappers[i].isIgnore())
                    continue;
                ret.put(mappers[i].getName(), mappers[i].getType().convert(row[i]));
            } catch (ArrayIndexOutOfBoundsException aioob) {
                //log.error("Extra cols in row" + Arrays.toString(row));
            }
        }
        ret.put("_msg", ret.values().stream().map(Object::toString).collect(Collectors.joining(", ")));
        log.trace("Read message: {}", ret);
        return ret;
    }

    @Override
    public List<String> getFieldNames() {
        return conf.getFieldMappings().values().stream().map(DocField::getName).collect(Collectors.toList());
    }

    @Override
    public List<DocField> getFields() {
        return new LinkedList<>(conf.getFieldMappings().values());
    }
}
