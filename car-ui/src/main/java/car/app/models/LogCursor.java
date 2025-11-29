//package car.app.models;
//
//import car.engine.recordreader.RecordReader;
//import lombok.Builder;
//import lombok.Data;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.RandomAccessFile;
//
//@Data
//@Builder
//public class LogCursor {
//
//    String fileName;
//    RecordReader rr;
//    RandomAccessFile rf;
//
//    public LogCursor(String path, RecordReader rr) throws FileNotFoundException {
//
//        this.fileName = path;
//        rf = new RandomAccessFile(path, "r");
//
//        rr.setFileName(path);
//
//
//
//    }
//
//}
