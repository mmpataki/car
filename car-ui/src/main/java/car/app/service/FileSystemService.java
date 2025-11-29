package car.app.service;

import car.common.CarCommonConfig;
import car.fs.FileManager;
import car.fs.FileStat;
import car.fs.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class FileSystemService {

    FileManager fm = FileManager.getInstance();

    public FileSystemService() throws Exception {
    }

    public Map<String, Long> addFile(String dsetId, String fileName, InputStream is) throws Exception {
        return fm.addFile(dsetId, fileName, is);
    }

    public List<FileStat> listFiles(String dsetId) throws Exception {
        return fm.listFiles(dsetId);
    }

    public File downloadFiles(String dsetId, List<String> files) throws Exception {
        return fm.downloadFiles(dsetId, files);
    }

    public void deleteFiles(String dsetId, List<String> files) throws Exception {
        fm.deleteFiles(dsetId, files);
    }
}
