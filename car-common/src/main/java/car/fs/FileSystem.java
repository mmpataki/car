package car.fs;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public abstract class FileSystem {

    /**
     * Opens a file for reading.
     */
    public abstract InputStream open(String path) throws Exception;


    /**
     * Creates an empty file
     */
    public abstract OutputStream create(String path) throws Exception;


    /**
     * Lists file in a directory
     */
    public abstract List<FileStat> list(String path) throws Exception;


    /**
     * Delete file
     */
    public abstract void delete(String path) throws Exception;


    @Data
    @Builder
    public static class Partition {

        /* position in the file where the read should start */
        public long start;

        /* this is an optional String */
        public List<String> hosts;
    }

    /**
     * Returns list of partitions of a file. One can pass number of desired partitions but
     * implementations are free to override it.
     */
    public abstract List<Partition> getPartitions(String path, int desiredPartitions) throws Exception;

}
