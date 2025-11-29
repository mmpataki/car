package car.engine.store;

import car.engine.query.Query;
import car.engine.rules.Rule;
import car.engine.rules.types.DocField;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class SqlStore extends Store<StoreResponse, Document, Query> {

    long processed = 0;
    Connection conn;

    public SqlStore(String id) throws Exception {
        super(id);
    }

    @Override
    public void init() throws Exception {
        conn = getConnection();
        create();
    }

    public abstract Connection getConnection() throws Exception;

    public abstract void create() throws Exception;

    @Override
    public StoreResponse search(Query q) throws Exception {

        if(conn.isClosed()) {
            log.info("Connection to {} was closed, reopening", id);
            init();
        }

        conn.setSchema(getId());

        Statement stmt = conn.createStatement();
        List<Object[]> ret = new ArrayList<>();
        String[] colNames = null;

        if (stmt.execute(q.getQuery())) {

            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData md = rs.getMetaData();
            int rowSize = md.getColumnCount();

            colNames = new String[rowSize];
            for (int i = 1; i <= rowSize; i++) {
                colNames[i - 1] = md.getColumnLabel(i);
            }

            while (rs.next()) {
                Object[] row = new Object[rowSize];
                for (int i = 1; i <= rowSize; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                ret.add(row);
            }
        }

        return StoreResponse.builder().data(ret).fieldNames(colNames).build();
    }

    @Override
    public void put(Document doc) throws Exception {
        put(Collections.singletonList(doc));
    }

    @Override
    public void put(List<Document> docs) throws Exception {
        processed += docs.size();
        Map<String, List<Document>> grouped = docs.stream().collect(Collectors.groupingBy(Document::getType));
        for (Map.Entry<String, List<Document>> docTypeAndSet : grouped.entrySet()) {
            String type = docTypeAndSet.getKey();
            List<Document> docset = docTypeAndSet.getValue();
            if (true) {
                log.trace("Docs of type: " + type);
                docset.forEach(d -> log.trace(d.toString()));
            }
            PreparedStatement pstmt = conn.prepareStatement(makeInsertSql(docset.get(0)));
            for (Document doc : docset) {
                int i = 1;
                for (Object v : doc.getData().values()) {
                    try {
                        pstmt.setObject(i++, v);
                    } catch (Exception e) {
                        log.error("pstmt=" + pstmt + "\nmap=" + doc);
                        throw e;
                    }
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }


    @Override
    public void close() throws Exception {
        conn.commit();
        conn.close();
        log.info("Processed " + processed + " docs");
    }

    @Override
    public Object getMetadata() throws Exception {
        Map<String, List<Column>> ret = new HashMap<>();
        ResultSet rset = conn.getMetaData().getColumns(getId(), null, null, null);
        while (rset.next()) {
            String table_name = rset.getString("TABLE_NAME");
            if (!ret.containsKey(table_name)) {
                ret.put(table_name, new ArrayList<>());
            }
            List<Column> columns = ret.get(table_name);
            columns.add(Column.builder().name(rset.getString("COLUMN_NAME")).type(rset.getString("TYPE_NAME")).build());
        }
        return ret;
    }

    @Override
    public void reportFields(Map<String, List<DocField>> tables){
        tables.forEach((tableName, fields) -> {
            try {
                updateTableIfRequired(tableName, fields);
            } catch (Exception e) {
                log.error("Error while creating table for {}, ({})", tableName, fields, e);
            }
        });
    }

    public abstract void createEmptyTable(String tabName) throws Exception;

    public abstract String makeInsertSql(Document document) throws Exception;

    protected abstract void updateTableIfRequired(String tableName, List<DocField> fields) throws Exception;

}
