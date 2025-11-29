package car.app.service;

import car.app.CarAppConfiguration;
import car.app.service.api.NotificationPayLoad;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    CarAppConfiguration conf;

    Session session = null;

    @With
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class EmailObject implements NotificationPayLoad {
        String subject;
        String body;
    }

    @PostConstruct
    public void init() {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", conf.isEmailAuthEnabled());
        prop.put("mail.smtp.starttls.enable", conf.isEmailSslEnabled());
        prop.put("mail.smtp.host", conf.getEmailSmtpHost());
        prop.put("mail.smtp.port", conf.getEmailSmtpPort());
        prop.put("mail.smtp.ssl.trust", conf.isEmailSmtpTrust());
        session = Session.getInstance(prop);
    }

    public void send(String to, EmailObject eo) throws Exception {

        System.out.println("to = " + to + ", subject = " + eo.getSubject() + ", body = " + eo.getBody());

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(conf.getEmailSenderId()));
        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(to));
        message.setSubject(eo.getSubject());

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(eo.getBody(), "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);
        Transport.send(message);
    }

}
