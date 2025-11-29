package car.engine.store;

import car.engine.processor.Dataset;
import car.engine.recordreader.RecordReader;
import car.engine.rules.Rule;
import car.engine.rules.types.DateType;
import car.engine.rules.types.DocField;
import car.engine.rules.types.NumberType;
import car.engine.rules.types.StringType;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SqliteStore extends SqlStore {

    String dbPath;

    public SqliteStore(Dataset dset) throws Exception {
        super(dset.getName());
        dbPath = String.format("%s/.store.db", dset.getId());
    }

    @Override
    public Connection getConnection() throws Exception {
        conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s", dbPath), "", "");
        conn.setAutoCommit(false);
        return conn;
    }

    @Override
    public void create() throws Exception {

    }

    @Override
    public void createEmptyTable(String tabName) throws Exception {
        conn.createStatement().execute(String.format("create table if not exists %s (_rule string)", tabName));
    }

    @Override
    public String makeInsertSql(Document document) {
        String sql = String.format(
                "insert into %s (%s) values (%s)",
                document.getType(),
                document.keys().stream().collect(Collectors.joining(", ")),
                document.keys().stream().map(x -> "?").collect(Collectors.joining(", "))
        );
        return sql;
    }

    @Override
    public void updateTableIfRequired(String tableName, List<DocField> fields) throws Exception {
        Statement stmt = conn.createStatement();
        createEmptyTable(tableName);
        for (DocField field : fields) {
            try {
                String tml = String.format("alter table %s add column %s %s", tableName, field.getName(), dtype(field));
                stmt.execute(tml);
            } catch (Exception e) {
                if (!e.getMessage().contains("duplicate column name"))
                    throw e;
            }
        }
    }

    private String dtype(DocField field) {
        if (field.getType() instanceof DateType || field.getType() instanceof NumberType) {
            return "integer";
        }
        if (field.getType() instanceof StringType) {
            return "string";
        }
        throw new IllegalArgumentException("unsupported objects detected: " + field);
    }

    @Override
    public void delete() throws Exception {
        conn.close();
        Files.deleteIfExists(Paths.get(dbPath));
    }

}
