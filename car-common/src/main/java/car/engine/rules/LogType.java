package car.engine.rules;

import car.engine.detector.Detector;
import car.engine.recordreader.ReadConfig;
import car.engine.recordreader.RecordReader;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Builder
public class LogType {

    String id;

    String name;

    boolean mergeMultipleFiles;

    String fileMergingRegex, mergedFileName;

    String description;

    List<Detector> detectors;

    transient List<Rule> rules = new ArrayList<>();

    RecordReader recordReader;

    boolean structured;

    ReadConfig readConfig;

    String defaultSearchView;

    public RecordReader getRecordReader() {
        if (!structured) {
            return recordReader;
        } else {
            return readConfig.buildReader();
        }
    }


    /* adds a merged file name before actual file name*/
    public String normalizeFileName(String fPath) {
        String sep = fPath.contains("/") ? "/" : fPath.contains("\\") ? "\\" : "";
        String s = mergeMultipleFiles
                ? sep.isEmpty()
                ? createMergedFileName(fPath)
                : fPath.substring(0, fPath.lastIndexOf(sep)) + sep + createMergedFileName(fPath)// + fPath.substring(fPath.lastIndexOf(sep))
                : fPath;
        return s;
    }

    private String createMergedFileName(String path) {
        if(fileMergingRegex == null)
            return mergedFileName;
        Matcher pathPattern = Pattern.compile(fileMergingRegex).matcher(path);
        if(!pathPattern.matches())
            return path;
        Matcher m = Pattern.compile("\\$\\{([0-9]+)\\}").matcher(mergedFileName);
        StringBuffer sb = new StringBuffer();
        int i = 0, last  =0;
        while(m.find()) {
            ++i;
            String num = m.group(1);
            int start = m.start(1) - 2;
            sb.append(mergedFileName, last, start).append(pathPattern.group(Integer.parseInt(num)));
            last = start + num.length() + 3;
        }
        sb.append(mergedFileName.substring(last));
        return sb.toString();
    }

    public static void main(String[] args) {
        String mergedFileName = LogType.builder().fileMergingRegex("dis_(.*).log.([0-9]+)").mergedFileName("dis_${1}_${1}__${2}.log").build().createMergedFileName("dis_MYDIS.log.1");
        System.out.println(mergedFileName);
    }

    /* returns the normalized file name without the actual file name part */
    public String indexFileName(String fPath) {
        String sep = fPath.contains("/") ? "/" : fPath.contains("\\") ? "\\" : "";
        String s = mergeMultipleFiles
                ? sep.isEmpty()
                ? createMergedFileName(fPath)
                : fPath.substring(0, fPath.lastIndexOf(sep)) + sep + createMergedFileName(fPath)
                : fPath;
        return s;
    }
}
