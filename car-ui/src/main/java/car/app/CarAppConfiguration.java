package car.app;

import car.app.service.EmailService;
import car.common.Argument;
import car.common.Configuration;
import car.engine.processor.ProcessingEngine;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Getter
@Component
public class CarAppConfiguration extends Configuration {

    @Argument(keys = {"--app.db.driver"}, help = "db jdbc driver")
    String appDbDriver;

    @Argument(keys = {"--app.db.url"}, help = "db jdbc url")
    String appDbUrl;

    @Argument(keys = {"--app.db.user"}, help = "db username")
    String appDbUser;

    @Argument(keys = {"--app.db.password"}, help = "db password")
    String appDbPassword;

    @Argument(keys = {"--app.processing.engine.class"}, help = "processing engine class name", parser = "classInstatiator", defValue = "car.engine.processor.LocalProcessor")
    ProcessingEngine processingEngine;

    @Argument(keys = {"--deployment.master.url"}, help = "URL where rules are hosted", defValue = "")
    String deploymentMasterUrl;

    @Argument(keys = {"--deployment.am.i.master"}, help = "does this server act as a master")
    boolean master;

    @Argument(keys = {"--storageservice.root.dir"}, help = "root dir where all files of user are stored")
    String storageServiceRoot;

    @Argument(keys = {"--emailservice.auth.enabled"}, help = "Email service authentication enabled")
    boolean emailAuthEnabled;

    @Argument(keys = {"--emailservice.ssl.enabled"}, help = "Email service ssl enabled")
    boolean emailSslEnabled;

    @Argument(keys = {"--emailservice.smtp.host"}, help = "Email service SMTP hostname")
    String emailSmtpHost;

    @Argument(keys = {"--emailservice.smtp.port"}, help = "Email service SMTP port")
    int emailSmtpPort;

    @Argument(keys = {"--emailservice.smtp.trust"}, help = "Email service trust smtp")
    boolean emailSmtpTrust;

    @Argument(keys = {"--emailservice.sender.email.id"}, help = "Email service sender email id")
    String emailSenderId;


    public CarAppConfiguration(String[] args) throws Exception {
        super(args);
    }

    public CarAppConfiguration() throws Exception {
        super();
    }
}
