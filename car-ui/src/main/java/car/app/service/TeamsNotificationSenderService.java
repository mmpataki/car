package car.app.service;

import car.app.CarAppConfiguration;
import car.app.service.api.NotificationPayLoad;
import com.google.gson.Gson;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

@Service
public class TeamsNotificationSenderService {

    @Autowired
    CarAppConfiguration conf;

    Session session = null;

    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class TeamsNotification implements NotificationPayLoad {
        String body;
    }

    public void send(String s_url, TeamsNotification not) throws Exception {
        URL url = new URL(s_url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.getOutputStream().write(getBytes(not));

        InputStream in = connection.getInputStream();
        byte[] buf = new byte[4096];
        int length;
        while ((length = in.read(buf)) > 0) {
            System.out.write(buf, 0, length);
        }
    }

    private byte[] getBytes(TeamsNotification not) {
        return new Gson().toJson(Collections.singletonMap("text", not.getBody())).getBytes();
    }

}
