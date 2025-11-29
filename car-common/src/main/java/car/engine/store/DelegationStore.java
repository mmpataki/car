package car.engine.store;

import car.engine.recordreader.RecordReader;
import car.engine.rules.Rule;
import car.engine.rules.types.DocField;

import java.util.List;
import java.util.Map;

public class DelegationStore<R, I, Q> extends Store<R, I, Q> {

    private Store<R, I, Q> delegate;

    public DelegationStore(Store<R, I, Q> store) {
        super(store.getId());
        this.delegate = store;
    }

    @Override
    public void init() throws Exception {
        delegate.init();
    }

    @Override
    public R search(Q q) throws Exception {
        return delegate.search(q);
    }

    @Override
    public void put(I doc) throws Exception {
        delegate.put(doc);
    }

    @Override
    public void put(List<I> docs) throws Exception {
        delegate.put(docs);
    }

    @Override
    public void close() throws Exception {
        delegate.close();
    }

    @Override
    public void delete() throws Exception {
        delegate.delete();
    }

    @Override
    public Object getMetadata() throws Exception {
        return delegate.getMetadata();
    }

    @Override
    public void reportFields(Map<String, List<DocField>> fields) {
        delegate.reportFields(fields);
    }

}
