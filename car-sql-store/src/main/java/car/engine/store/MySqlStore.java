package car.engine.store;

import car.common.Argument;
import car.common.Configuration;
import car.engine.recordreader.RecordReader;
import car.engine.rules.Rule;
import car.engine.rules.types.DateType;
import car.engine.rules.types.DocField;
import car.engine.rules.types.NumberType;
import car.engine.rules.types.StringType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MySqlStore extends SqlStore {

    @Getter
    class MySqlStoreConfig extends Configuration {
        @Argument(keys = {"--store.mysqlstore.url"}, help = "mysql jdbc url")
        String url;

        @Argument(keys = {"--store.mysqlstore.user"}, help = "mysql username")
        String user;

        @Argument(keys = {"--store.mysqlstore.password"}, help = "mysql password")
        String password;

        public MySqlStoreConfig(String[] args) throws Exception {
            super(args);
        }

        public MySqlStoreConfig() throws Exception {
            super();
        }
    }

    MySqlStoreConfig conf = new MySqlStoreConfig();

    public MySqlStore(String datasetName) throws Exception {
        super(datasetName);
    }

    @Override
    public Connection getConnection() throws Exception {
        Class<?> aClass = Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(conf.getUrl(), conf.getUser(), conf.getPassword());
        conn.setAutoCommit(false);
        return conn;
    }

    @Override
    public void create() throws Exception {
        try {
            conn.createStatement().execute(String.format("create schema %s", getId()));
        } catch (Exception e) {
            if (!e.getMessage().contains("database exists"))
                throw new Exception(e);
        } finally {
            conn.createStatement().execute(String.format("use %s", getId()));
        }
    }

    @Override
    public void createEmptyTable(String tabName) throws Exception {
        try {
            conn.createStatement().execute(String.format("create table %s (_rule MEDIUMTEXT)", tabName));
        } catch (Exception e) {
            if (!e.getMessage().contains("already exists"))
                throw new Exception(e);
        }
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
                if (!e.getMessage().contains("Duplicate column name"))
                    throw e;
            }
        }
    }

    private String dtype(DocField field) {
        if (field.getType() instanceof DateType || field.getType() instanceof NumberType) {
            return "BIGINT";
        }
        if (field.getType() instanceof StringType) {
            return "MEDIUMTEXT";
        }
        throw new IllegalArgumentException("unsupported objects detected: " + field);
    }

    @Override
    public void delete() throws Exception {
        try {
            conn.createStatement().execute(String.format("drop schema %s", getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
