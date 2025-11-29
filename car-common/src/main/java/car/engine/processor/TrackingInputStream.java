package car.engine.processor;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;

@Getter
public class TrackingInputStream extends InputStream {

    long bytesRead = 0;
    InputStream inputStream;

    public TrackingInputStream(InputStream is) {
        this.inputStream = is;
    }

    @Override
    public int read() throws IOException {
        bytesRead++;
        return inputStream.read();
    }

    public void reset() {
        bytesRead = 0;
    }
}