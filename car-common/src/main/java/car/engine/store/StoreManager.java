package car.engine.store;

import car.common.CarCommonConfig;
import car.engine.models.SearchQuery;
import car.engine.models.SearchResponse;
import car.engine.query.Query;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class StoreManager {

    CarCommonConfig conf = new CarCommonConfig();

    class Stores<R, I, Q> {

        String klass;
        private final Map<String, Store<R, I, Q>> stores = new ConcurrentHashMap<>();
        private final Map<String, AtomicReference<Integer>> storeUsers = new HashMap<>();

        public Stores(String klass) {
            this.klass = klass;
        }

        class ManagedStore extends DelegationStore<R, I, Q> {

            public ManagedStore(Store<R, I, Q> delegate) {
                super(delegate);
            }

            @Override
            public void close() throws Exception {
                synchronized (Stores.this) {
                    storeUsers.get(getId()).getAndUpdate(integer -> integer - 1);
                    if (storeUsers.get(getId()).get() == 0) {
                        stores.remove(getId());
                        super.close();
                    }
                }
            }

            @Override
            public void delete() throws Exception {
                super.delete();
            }
        }


        public synchronized Store<R, I, Q> _getStore(String dsetId) throws Exception {
            AtomicReference<Exception> ex = new AtomicReference<>();
            Store<R, I, Q> store = stores.computeIfAbsent(dsetId, id -> {
                try {
                    ManagedStore riqManagedStore = new ManagedStore((Store<R, I, Q>) Class.forName(klass).getConstructor(String.class).newInstance(id));
                    storeUsers.put(id, new AtomicReference<>(0));
                    riqManagedStore.init();
                    return riqManagedStore;
                } catch (Exception e) {
                    ex.set(e);
                    return null;
                }
            });
            if (store == null) {
                throw ex.get();
            }
            storeUsers.get(dsetId).getAndUpdate(i -> i + 1);
            return store;
        }


        public void deleteStore(String datasetId) throws Exception {
            Store<R, I, Q> removed = stores.remove(datasetId);
            if (removed == null)
                removed = _getStore(datasetId);
            removed.delete();
        }
    }

    Stores<StoreResponse, Document, Query> relStores = new Stores<>(conf.getStoreClass());
    Stores<SearchResponse, Document, SearchQuery> idxStores = new Stores<>(conf.getIndexStoreClass());

    public StoreManager() throws Exception {
    }

    public void deleteStores(String datasetId) throws Exception {
        relStores.deleteStore(datasetId);
        idxStores.deleteStore(datasetId);
    }

    public Store<StoreResponse, Document, Query> getStore(String id) throws Exception {
        return relStores._getStore(id);
    }

    public Store<SearchResponse, Document, SearchQuery> getIndexStore(String dsetId) throws Exception {
        return idxStores._getStore(dsetId);
    }

}
