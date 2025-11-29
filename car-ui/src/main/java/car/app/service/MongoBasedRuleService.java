//package car.app.service;
//
//import car.engine.rules.LogType;
//import car.engine.rules.LogTypeGroup;
//import car.engine.rules.Rule;
//import com.mongodb.MongoClient;
//import dev.morphia.Datastore;
//import dev.morphia.Morphia;
//import dev.morphia.annotations.Entity;
//import dev.morphia.annotations.Id;
//import lombok.*;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
////@Service
//public class MongoBasedRuleService implements RuleService {
//
//    Datastore dstore;
//
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Getter
//    @Setter
//    @Builder
//    @Entity("car-meta")
//    public static class MorphiaWrapper<T> {
//        @Id
//        String id;
//
//        String parent;
//
//        T obj;
//    }
//
//
//    @PostConstruct
//    public void init() {
//
//        String pkgName = this.getClass().getPackage().getName();
//        MongoClient mclient = new MongoClient("localhost", 27017);
//
//        Morphia morphia = new Morphia();
//        morphia.mapPackage(pkgName);
//
//        dstore = morphia.createDatastore(mclient, "car");
//
//        dstore.ensureIndexes();
//    }
//
//    private <T> T findOne(String id) {
//        Class<MorphiaWrapper<T>> type = (Class<MorphiaWrapper<T>>) (Object) MorphiaWrapper.class;
//        MorphiaWrapper<T> mw = dstore.createQuery(type)
//                .filter("id = ", id)
//                .find()
//                .tryNext();
//        return mw == null ? null : mw.getObj();
//    }
//
//    private <T> List<T> findChildrenOf(String parentId) {
//        Class<MorphiaWrapper<T>> type = (Class<MorphiaWrapper<T>>) (Object) MorphiaWrapper.class;
//        List<MorphiaWrapper<T>> mw = dstore.createQuery(type)
//                .filter("parent = ", parentId)
//                .find()
//                .toList();
//        return mw.stream().map(mwi -> mwi.getObj()).collect(Collectors.toList());
//    }
//
//    String makeLogTypeGroupParent() {
//        return "";
//    }
//
//    String makeLogTypeGroupId(String id) {
//        return "/" + id;
//    }
//
//    @Override
//    public LogTypeGroup getLogTypeGroup(String name) {
//        return findOne(makeLogTypeGroupId(name));
//    }
//
//
//    @Override
//    public List<LogTypeGroup> getLogTypeGroups() {
//        return findChildrenOf(makeLogTypeGroupParent());
//    }
//
//    @Override
//    public List<LogType> getLogTypes(String group) {
//        return findChildrenOf(makeLogTypeGroupId(group));
//    }
//
//    @Override
//    public void registerLogTypeGroup(LogTypeGroup grp) throws IOException {
//        dstore.save(
//                MorphiaWrapper.builder()
//                        .parent(makeLogTypeGroupParent())
//                        .obj(grp)
//                        .id(makeLogTypeGroupId(grp.getName()))
//                        .build()
//        );
//    }
//
//    @Override
//    public void addLogType(String group, LogType type) throws IOException {
//        dstore.save(
//                MorphiaWrapper.builder()
//                        .obj(type)
//                        .parent(makeLogTypeGroupId(group))
//                        .id(makeLogTypeId(group, type.getName()))
//                        .build()
//        );
//    }
//
//    private String makeLogTypeId(String group, String name) {
//        return makeLogTypeGroupId(group) + "/" + name;
//    }
//
//    @Override
//    public void addRule(String group, String type, Rule rule) throws IOException {
//        dstore.save(
//                MorphiaWrapper.builder()
//                        .obj(rule)
//                        .id(makeRuleId(group, type, rule.getName()))
//                        .parent(makeLogTypeId(group, type))
//                        .build()
//        );
//    }
//
//    private String makeRuleId(String group, String type, String name) {
//        return makeLogTypeId(group, type) + "/rules/" + name;
//    }
//
//    @Override
//    public List<Rule> getRules(String group, String type) {
//        return findChildrenOf(makeLogTypeId(group, type) + "/rules");
//    }
//}
