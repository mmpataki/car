package car.common;

import lombok.Getter;

@Getter
public class CarCommonConfig extends Configuration {

    @Argument(keys = {"--store.class"}, help = "The implementation to use to connect to store", required = true)
    private String storeClass;

    @Argument(keys = {"--index.store.class"}, help = "The implementation to use to connect to index store", required = true)
    private String indexStoreClass;

    @Argument(keys = {"--fs.class"}, help = "The implementation to use to manage files", required = true)
    private String fileSystemClass;

    @Argument(keys = {"--car.app.url"}, help = "The CAR URL (since there can be many interfaces, specify this explicitly)", required = true)
    private String carUrl;


    public CarCommonConfig(String[] args) throws Exception {
        super(args);
    }

    public CarCommonConfig() throws Exception {
        super();
    }
}
