package car.fs;

import car.common.Argument;
import car.common.CarCommonConfig;
import com.mmp.sdfs.client.SdfsClient;
import com.mmp.sdfs.common.DnAddress;
import com.mmp.sdfs.common.LocatedBlock;
import com.mmp.sdfs.conf.SdfsClientConfig;
import lombok.Getter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CarSDFileSystem extends FileSystem {

    @Getter
    static class CarSDFSConfig extends CarCommonConfig {

        @Argument(keys = {"--sdfs.headnode.host"}, help = "SDFS Namenode host", required = true)
        String nameNodeHost;

        @Argument(keys = {"--sdfs.headnode.port"}, help = "SDFS Namenode port", required = true)
        int nameNodePort;

        @Argument(keys = {"--sdfs.block.size"}, help = "Block size", required = true)
        int blockSize;

        public CarSDFSConfig(String[] args) throws Exception {
            super(args);
        }

        public CarSDFSConfig() throws Exception {
            super();
        }
    }

    CarSDFSConfig cconf = new CarSDFSConfig();
    SdfsClient client;

    public CarSDFileSystem() throws Exception {
        SdfsClientConfig conf = new SdfsClientConfig(new String[]{
                "--nnhost",
                cconf.getNameNodeHost(),
                "--nnport",
                String.valueOf(cconf.getNameNodePort()),
                "--blockSize",
                String.valueOf(cconf.getBlockSize())
        });
        client = new SdfsClient(conf);
    }


    @Override
    public InputStream open(String path) throws Exception {
        return client.open(path);
    }

    @Override
    public OutputStream create(String path) throws Exception {
        try {
            return client.create(path);
        } catch (Exception e) {
            if(e.getMessage().equals("File exists")) {
                client.delete(path);
                return client.create(path);
            }
            throw e;
        }
    }

    @Override
    public List<FileStat> list(String path) throws Exception {
        return client.list(path).stream()
                .map(fs -> FileStat.builder().path(fs.getPath().substring(path.length() + 1)).length(fs.getSize()).build())
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String path) throws Exception {
        client.delete(path);
    }

    @Override
    public List<Partition> getPartitions(String path, int desiredPartitions) throws Exception {
        List<LocatedBlock> blocks = client.getBlocks(path);
        List<Partition> partitions = new LinkedList<>();
        for (int i = 0, blocksSize = blocks.size(); i < blocksSize; i++) {
            LocatedBlock block = blocks.get(i);
            partitions.add(
                    Partition.builder()
                            .start((long) i * cconf.getBlockSize())
                            .hosts(block.getLocations().stream().map(DnAddress::getId).collect(Collectors.toList()))
                            .build()
            );
        }
        return partitions;
    }
}
