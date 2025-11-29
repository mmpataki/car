package car.engine.store;

import car.common.Argument;
import car.common.Configuration;
import car.engine.models.LogMessage;
import car.engine.models.SearchQuery;
import car.engine.models.SearchResponse;
import car.engine.rules.types.DateType;
import car.engine.rules.types.DocField;
import car.engine.rules.types.FieldType;
import car.engine.rules.types.NumberType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudHttp2SolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SolrStore extends Store<SearchResponse, Document, SearchQuery> {

    @Getter
    class SolrStoreConfig extends Configuration {

        @Argument(keys = {"--searchservice.solr.url"}, help = "Solr server url")
        String url;

        public SolrStoreConfig(String[] args) throws Exception {
            super(args);
        }

        public SolrStoreConfig() throws Exception {
            super();
        }
    }

    SolrStoreConfig conf = new SolrStoreConfig();

    CloudHttp2SolrClient solr;

    public SolrStore(String id) throws Exception {
        super(id);
        solr = new CloudHttp2SolrClient.Builder(Arrays.asList(conf.getUrl().split(","))).build();
    }

    @Override
    public void init() {
        try {
            CollectionAdminRequest.createCollection(getId(), 1, 1).process(solr);
        } catch (Exception e) {
            if (e.getMessage().contains("collection already exists")) {
                log.info("collection {} already exists", getId());
            } else {
                log.error("Error while creating collection {}", getId(), e);
            }
        }
        solr.setDefaultCollection(getId());
    }

    private String getSearchQuery(SearchQuery sq) throws Exception {
        return sq.getQuery().entrySet().stream()
                .map(entry -> String.format("%s:(%s)", entry.getKey(), entry.getValue().replace("\\", "\\\\")))
                .collect(Collectors.joining(" AND "));
    }

    public void tryAndIgnoreEror(Runnable r) {
        try {
            r.run();
        } catch (Exception ignored) {
        }
    }

    @Override
    public SearchResponse search(SearchQuery sq) throws Exception {

        int start = sq.getCursor() == null ? 0 : Integer.parseInt(sq.getCursor());
        QueryResponse response = doQuery(start, sq);

        Map<String, Map<String, Long>> facets = null;
        if (sq.isFacet()) {
            facets = new HashMap<>();
            Map<String, Map<String, Long>> finalFacets = facets;
            if (response.getFacetFields() != null) {
                response.getFacetFields().forEach(ff -> {
                    HashMap<String, Long> fvMap = new HashMap<>();
                    finalFacets.put(ff.getName(), fvMap);
                    ff.getValues().forEach(fv -> {
                        fvMap.put(fv.getName(), fv.getCount());
                    });
                });
            }
        }

        List<LogMessage> ret = new ArrayList<>();
        for (SolrDocument hit : response.getResults()) {
            LogMessage lm = new LogMessage();
            for (String fieldName : hit.getFieldNames()) {
                lm.put(fieldName, hit.getFirstValue(fieldName));
            }
            ret.add(lm);
        }

        Map<String, Map<String, Integer>> rangeFacets = new HashMap<>();
        if (response.getFacetRanges() != null) {
            response.getFacetRanges().forEach(rangeFacet -> {
                Map<String, Integer> rfacets;
                rangeFacets.put(rangeFacet.getName(), rfacets = new HashMap<>());
                for (Object ocount : rangeFacet.getCounts()) {
                    RangeFacet.Count count = (RangeFacet.Count) ocount;
                    rfacets.put(count.getValue(), count.getCount());
                }
            });
        }

        Map<String, Map<String, Object>> stats = new HashMap<>();
        if (response.getFieldStatsInfo() != null) {
            response.getFieldStatsInfo().forEach((field, fsi) -> {
                Map<String, Object> stat;
                stats.put(field, stat = new HashMap<>());
                stat.put("count", fsi.getCount());
                stat.put("min", fsi.getMin());
                stat.put("max", fsi.getMax());
                if (fsi.getMean() != null && !Double.isNaN((double) fsi.getMean()))
                    stat.put("mean", fsi.getMean());
                stat.put("sum", fsi.getSum());
                if (!Double.isNaN(fsi.getStddev()))
                    stat.put("stddev", fsi.getStddev());
            });
        }

        return SearchResponse.builder()
                .msgs(ret)
                .cursor(start + sq.getPageSize() + "")
                .totalHits(response.getResults().getNumFound())
                .stats(stats)
                .facets(facets)
                .rangeFacets(rangeFacets)
                .build();
    }

    private QueryResponse doQuery(int start, SearchQuery sq) throws Exception {
        sq.getQuery().put("dset", getId());
        String bq = getSearchQuery(sq);
        SolrQuery query = new SolrQuery();

        query.set("q", bq);
        query.setStart(start);
        query.setRows(sq.getPageSize());

        if (sq.getSortFields() != null) {
            sq.getSortFields().forEach(sp -> {
                query.addSort(
                        sp.getSecond().equalsIgnoreCase("asc")
                                ? SolrQuery.SortClause.asc(sp.getFirst())
                                : SolrQuery.SortClause.desc(sp.getFirst())
                );
            });
        }

        if (sq.isFacet()) {
            query.setFacet(true);
            query.setFacetMinCount(1);
            query.addFacetField(sq.getFacetFields().toArray(new String[0]));
            tryAndIgnoreEror(() -> query.removeFacetField("_line"));
            tryAndIgnoreEror(() -> query.removeFacetField("ts"));
            tryAndIgnoreEror(() -> query.removeFacetField("_msg"));
        }

        if (sq.getFields() != null && !sq.getFields().isEmpty()) {
            sq.getFields().forEach(query::addField);
        }

        if (sq.getRangeFacets() != null) {
            for (SearchQuery.RangeFacetQuery rf : sq.getRangeFacets()) {
                query.addNumericRangeFacet(rf.getField(), rf.getStart(), rf.getEnd(), rf.getGap());
            }
        }

        if (sq.getStatFields() != null) {
            sq.getStatFields().forEach(query::addGetFieldStatistics);
        }

        log.info("Search query: " + query);
        return solr.query(query);
    }

    @Override
    public void put(Document doc) throws Exception {
        put(Collections.singletonList(doc));
    }

    @Override
    public void put(List<Document> docs) throws Exception {
        List<SolrInputDocument> sdocs = docs.stream().map(doc -> {
            SolrInputDocument sdoc = new SolrInputDocument();
            for (Map.Entry<String, Object> e : doc.getData().entrySet()) {
                sdoc.addField(e.getKey(), e.getValue());
            }
            sdoc.addField("dset", this.getId());
            return sdoc;
        }).collect(Collectors.toList());
        try {
            solr.add(getId(), sdocs);
        } catch (Exception e) {
            log.error("Error while putting solr doc", e);
            log.error("Doc " + sdocs);
        }
        solr.commit();
    }

    @Override
    public void close() throws Exception {
        solr.commit();
    }

    @Override
    public void delete() throws Exception {
        CollectionAdminRequest.deleteCollection(getId()).process(solr);
    }

    @Override
    public Object getMetadata() throws Exception {
        SchemaRequest.Fields fields = new SchemaRequest.Fields();
        List<Map<String, Object>> process = fields.process(solr).getFields();

        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("text_general", "String");
        typeMap.put("plongs", "Long");
        typeMap.put("string", "String");

        Set<String> blackListedFields = new HashSet<>(Arrays.asList("_nest_path_", "_root_", "_text_", "_version_"));

        Map<String, Set<Column>> ret = new HashMap<>();
        Set<Column> cols = new HashSet<>();
        for (Map<String, Object> result : process) {
            if (!blackListedFields.contains((String) result.get("name")))
                cols.add(Column.builder().name((String) result.get("name")).type(typeMap.get((String) result.get("type"))).build());
        }
        ret.put("schema", cols);
        System.out.println(ret);
        return ret;
    }

    @Override
    public void reportFields(Map<String, List<DocField>> fields) {
        fields.values().stream().flatMap(Collection::stream)
                .map(f -> {
                    System.out.println(f);
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", f.getName());
                    m.put("type", mapType(f.getType()));
                    m.put("multiValued", false);
                    m.put("stored", true);
                    m.put("indexed", true);
                    m.put("required", false);
                    return m;
                })
                .map(SchemaRequest.AddField::new)
                .forEach(a -> {
                    try {
                        a.process(solr);
                    } catch (Exception e) {
                        log.warn("Error while creating fields: {}, {}", a, e.getMessage());
                        log.debug("Exception", e);
                    }
                });
    }

    private String mapType(FieldType type) {
        if (type instanceof DateType)
            return "plongs";
        if (type instanceof NumberType)
            return "DoublePointField";
        return "text_general";
    }
}
