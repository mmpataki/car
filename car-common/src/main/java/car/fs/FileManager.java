package car.fs;

import car.common.CarCommonConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileManager {

    private static String basedir = "datasets";
    private FileSystem fs;
    private static FileManager fm = null;

    private static String getDsetDir(String id) {
        return String.format("%s/%s", basedir, id);
    }

    private String makeFilePath(String dsetId, String filePath) {
        return String.format("%s/%s", getDsetDir(dsetId), filePath);
    }

    private FileManager(FileSystem fs) {
        this.fs = fs;
    }

    /* PUBLIC API */
    public synchronized static FileManager getInstance() throws Exception {
        if (fm != null)
            return fm;

        CarCommonConfig conf = new CarCommonConfig();
        fm = new FileManager((FileSystem) Class.forName(conf.getFileSystemClass()).newInstance());

        return fm;
    }


    public Map<String, Long> addFile(String dsetId, String fileName, InputStream is) throws Exception {
        boolean isArchive = Stream.of(".tz", ".tar", ".zip", ".gz", ".7z", ".tgz").anyMatch(fileName::contains);
        Map<String, Long> addedFiles = new HashMap<>();
        if (isArchive) {
            try (ArchiveInputStream i = new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(is))) {
                ArchiveEntry entry;
                while ((entry = i.getNextEntry()) != null) {
                    if (!i.canReadEntryData(entry)) {
                        continue;
                    }
                    String relativePath = String.format("%s/%s", fileName, entry.getName());
                    String absolutePath = makeFilePath(dsetId, relativePath);

                    if (!entry.isDirectory()) {
                        try (OutputStream o = fs.create(absolutePath)) {
                            long len = IOUtils.copy(i, o);
                            addedFiles.put(relativePath, len);
                        }
                    }
                }
            }
        } else {
            try (OutputStream o = fs.create(makeFilePath(dsetId, fileName))) {
                long len = IOUtils.copy(is, o);
                addedFiles.put(fileName, len);
            }
        }
        return addedFiles;
    }

    public List<FileStat> listFiles(String dsetId) throws Exception {
        return fs.list(getDsetDir(dsetId));
    }

    public File downloadFiles(String dsetId, List<String> files) throws Exception {
        File outFile = Files.createTempFile(
                dsetId + "_",
                files.size() == 1 ? "_" + new File(files.get(0)).getName() : ".zip"
        ).toFile();
        if (files.size() == 1) {
            try (InputStream in = fs.open(makeFilePath(dsetId, files.get(0)));
                 FileOutputStream out = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } else {
            try (FileOutputStream out = new FileOutputStream(outFile);
                 ZipOutputStream zipOut = new ZipOutputStream(out)) {
                for (String file : files) {
                    try (InputStream in = fs.open(makeFilePath(dsetId, file))) {
                        ZipEntry entry = new ZipEntry(file);
                        zipOut.putNextEntry(entry);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            zipOut.write(buffer, 0, bytesRead);
                        }
                        zipOut.closeEntry();
                    } catch (Exception e) {
                        log.error("Couldn't add {} to zip", file, e);
                    }
                }
            }
        }
        return outFile;
    }

    public void deleteFiles(String dsetId, List<String> files) throws Exception {
        if (files.isEmpty()) {
            fs.delete(getDsetDir(dsetId));
        } else {
            Map<String, String> failed = new HashMap<>();
            for (String file : files) {
                try {
                    fs.delete(makeFilePath(dsetId, file));
                } catch (Exception e) {
                    failed.put(file, e.getMessage());
                }
            }
            if (!failed.isEmpty())
                throw new Exception("Failed to delete few files: " + failed);
        }
    }

    public InputStream open(String dsetId, String path) throws Exception {
        return fs.open(makeFilePath(dsetId, path));
    }

    public OutputStream create(String dsetId, String path) throws Exception {
        return fs.create(makeFilePath(dsetId, path));
    }

    public List<FileSystem.Partition> getPartitions(String dsetId, String path, int desiredPartitions) throws Exception {
        return fs.getPartitions(makeFilePath(dsetId, path), desiredPartitions);
    }
}
