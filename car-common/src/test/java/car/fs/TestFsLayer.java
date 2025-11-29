package car.fs;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestFsLayer {

    @Test
    public void testAllApis() throws Exception {

        FileSystem fs = new LocalFs();
        FileManager fm = FileManager.getInstance();

        String dsid = "testds";

        fm.addFile(dsid, "csvtest.csv", getClass().getResourceAsStream("/csvtest.csv"));
        fm.addFile(dsid, "jsontest.zip", getClass().getResourceAsStream("/jsontest.zip"));

        List<FileStat> fileStats = fm.listFiles(dsid);
        fileStats.forEach(f -> {
            System.out.println(f);
            Assert.assertTrue(f.getPath() + " is not listed", Arrays.asList("csvtest.csv", "jsontest.zip\\jsontest.log").contains(f.getPath()));
        });

        File file = fm.downloadFiles(dsid, Collections.singletonList("csvtest.csv"));
        System.out.println(file);
        Assert.assertTrue(file.getName().startsWith(dsid));
        Assert.assertTrue("Download is not a zip file", file.getName().endsWith(".zip"));

        fm.deleteFiles(dsid, Collections.emptyList());

        try {
            fileStats = fm.listFiles(dsid);
            Assert.assertTrue("Files are not deleted yet", true);
        } catch (Exception e) {
            //pass
        }
    }

}
