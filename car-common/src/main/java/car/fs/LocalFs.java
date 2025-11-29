package car.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LocalFs extends FileSystem {

    @Override
    public InputStream open(String path) throws IOException {
        return Files.newInputStream(Paths.get(path));
    }

    @Override
    public OutputStream create(String path) throws IOException {
        Path p = Paths.get(path);
        if (!p.getParent().toFile().exists() && !p.getParent().toFile().mkdirs()) {
            throw new IOException("Couldn't create parent directories");
        }
        return Files.newOutputStream(p);
    }

    @Override
    public List<FileStat> list(String path) throws IOException {
        Path dir = Paths.get(path);
        return Files.walk(dir).map(p -> {
            File f = p.toFile();
            if (!f.isDirectory())
                return FileStat.builder().path(dir.relativize(p).toString()).length(f.length()).createdDate(f.lastModified()).build();
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        if (!directory.delete()) {
            System.out.println("Failed to delete : " + directory);
        }
    }

    @Override
    public void delete(String path) throws Exception {
        deleteDirectory(new File(path));
    }

    @Override
    public List<Partition> getPartitions(String path, int desiredPartitions) {
        return null;
    }

}
