package car.engine.store;

import java.util.ArrayList;
import java.util.List;

public class BatchingStore<R, I, Q> extends DelegationStore<R, I, Q> {

    private Integer batchSize = 100;
    private List<I> batch = new ArrayList<>();

    public BatchingStore(Store<R, I, Q> delegate, Integer batchSize) {
        super(delegate);
        this.batchSize = batchSize;
    }

    @Override
    public void put(I doc) throws Exception {
        batch.add(doc);
        flush(false);
    }

    private synchronized void flush(boolean force) throws Exception {
        if ((!force && batch.size() < batchSize) || batch.size() == 0)
            return;
        List<I> b1 = this.batch;
        this.batch = new ArrayList<>();
        super.put(b1);
    }

    @Override
    public void put(List<I> docs) throws Exception {
        batch.addAll(docs);
        flush(false);
    }

    @Override
    public void close() throws Exception {
        flush(true);
        super.close();
    }
}
