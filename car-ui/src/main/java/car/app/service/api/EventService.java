package car.app.service.api;

import car.app.App;
import car.app.models.AppNotification;
import car.app.models.Group;
import car.app.models.User;
import car.app.repository.EventQueue;
import car.app.service.EmailService;
import car.app.service.TeamsNotificationSenderService;
import car.app.util.UrlUtil;
import lombok.*;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventService {

    @Autowired
    CollabService CS;

    @Autowired
    UserServiceUtil DS;

    @Autowired
    UserGroupService US;

    @Autowired
    EmailService ES;

    @Autowired
    TeamsNotificationSenderService TS;

    @Autowired
    EventQueue EQ;

    @Autowired
    AppNotificationService ANS;

    static EventService es;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @With
    @Builder
    static class Reciepient {
        String id;
        RecipientType typ;
    }

    enum RecipientType {
        USER, GROUP
    }

    interface RecipientResolver {
        Set<Reciepient> getUsers(EventData o) throws Exception;
    }

    interface EventToEmailNotificationTransformer {
        EmailService.EmailObject transform(EventData payload);
    }

    interface EventToTeamsNotificationTransformer {
        TeamsNotificationSenderService.TeamsNotification transform(EventData payload);
    }

    interface EventToAppNotificationTransfomer {
        AppNotification.NotificationData transform(EventData payload);
    }

    public enum Type {

        REGISTER_USER
                (EnumSet.of(NotificationType.EMAIL), es.getUserResolver(), es.getRegisterEmailTemplateBuilder(), null, null),

        USER_ADD_TO_GROUP
                (EnumSet.of(NotificationType.EMAIL), es.getUserResolver(), es.getUserAddedToGroupEmailBuilder(), null, null),

        NEW_COMMENT
                (EnumSet.of(NotificationType.EMAIL, NotificationType.APP_NOTIFICATION), es.getNewCommentWatcherResolver(), es.getNewCommentEmailBuilder(), null, es.getCommentAppNotificationBuilder()),

        ADMIN
                (EnumSet.of(NotificationType.EMAIL), o -> Collections.singleton(new Reciepient().withId(UserGroupService.ADMIN).withTyp(RecipientType.GROUP)), null, null, null),

        EXTERNAL_NOTIF
                (EnumSet.of(NotificationType.EMAIL, NotificationType.APP_NOTIFICATION, NotificationType.TEAMS_NOTIFICATION), es.getExternalEventRcptResolver(), es.getExternalEventEmailBuilder(), es.getExternalEventTeamsNotifBuilder(), es.getExternalEventAppNotifBuilder());
        ;

        EnumSet<NotificationType> notifTypes;


        RecipientResolver ur;
        EventToEmailNotificationTransformer eet;
        EventToTeamsNotificationTransformer ett;
        EventToAppNotificationTransfomer eat;

        Type(EnumSet<NotificationType> notifTypes, RecipientResolver ur, EventToEmailNotificationTransformer eet, EventToTeamsNotificationTransformer ett, EventToAppNotificationTransfomer eat) {
            this.notifTypes = notifTypes;
            this.ur = ur;
            this.eet = eet;
            this.ett = ett;
            this.eat = eat;
        }

        Set<Reciepient> resolveRecipients(EventData payload) throws Exception {
            return this.ur.getUsers(payload);
        }

        public TeamsNotificationSenderService.TeamsNotification getTeamsNotificationPayload(EventData payLoad) {
            if (ett != null)
                return ett.transform(payLoad);
            return null;
        }

        public EmailService.EmailObject getEmailPayload(EventData payLoad) {
            if (eet != null)
                return eet.transform(payLoad);
            return null;
        }

        public AppNotification.NotificationData getAppNotificationPayload(EventData payLoad) {
            if (eat != null)
                return eat.transform(payLoad);
            return null;
        }

        public EnumSet<NotificationType> getNotifTypes() {
            return notifTypes;
        }
    }

    @Data
    @Builder
    public static class Event {

        Type typ;
        EventData eventData;

        public Set<Reciepient> getReciepients() throws Exception {
            return typ.resolveRecipients(eventData);
        }
    }

    interface EventToPayloadTransformer {
        NotificationPayLoad generate(Event e);
    }

    interface NotificationSender {
        void send(NotificationPayLoad payload, Set<Reciepient> rcpnts);
    }

    static class EmailNotificationSender implements NotificationSender {

        @Override
        public void send(NotificationPayLoad payload, Set<Reciepient> rcpnts) {
            for (Reciepient r : rcpnts) {
                try {
                    String emailList = "";
                    if (r.getTyp() == RecipientType.GROUP) {
                        Group group = es.US.getGroup(r.getId());
                        if (group != null) {
                            if (group.getEmail() != null) {
                                emailList = group.getEmail();
                            } else {
                                emailList = group.getUsers().stream().map(uid -> {
                                    try {
                                        return es.US.getUser(uid);
                                    } catch (Exception e) {
                                        log.error("error while fetching users to send notifications ", e);
                                    }
                                    return null;
                                }).filter(x -> x != null).map(u -> u.getEmail()).filter(e -> e != null).collect(Collectors.joining(","));
                            }
                        }
                    } else {
                        User user = es.US.getUser(r.getId());
                        if (user != null)
                            emailList = user.getEmail();
                    }
                    if (!emailList.isEmpty())
                        es.ES.send(emailList, (EmailService.EmailObject) payload);
                } catch (Exception e) {
                    log.error("error while sending notification email for " + payload.toString() + " to " + r.getId(), e);
                }
            }
        }
    }

    static class TeamsNotificationSender implements NotificationSender {

        @Override
        public void send(NotificationPayLoad payload, Set<Reciepient> rcpnts) {
            for (Reciepient r : rcpnts) {
                try {
                    String url = null;
                    if (r.getTyp() == RecipientType.GROUP) {
                        Group group = es.US.getGroup(r.getId());
                        if (group != null) {
                            url = group.getTeamsUrl();
                        }
                    } else {
                        User usr = es.US.getUser(r.getId());
                        if (usr != null) {
                            url = usr.getTeamsUrl();
                        }
                    }
                    if (url != null) {
                        es.TS.send(url, (TeamsNotificationSenderService.TeamsNotification) payload);
                    }
                } catch (Exception e) {
                    log.error("error while sending notification email for " + payload.toString() + " to " + r.getId(), e);
                }
            }
        }
    }

    static class AppNotificationSender implements NotificationSender {

        @Override
        public void send(NotificationPayLoad payload, Set<Reciepient> rcpnts) {
            AppNotification.NotificationData anotif = (AppNotification.NotificationData) payload;
            try {
                es.ANS.postNotification(
                        rcpnts.stream().map(x -> x.getTyp() == RecipientType.GROUP
                                                ? UserServiceUtil.getGroupTag(x.getId())
                                                : UserServiceUtil.getUserTag(x.getId())
                                            ).collect(Collectors.toList()),
                        anotif
                );
            } catch (Exception e) {
                log.error("error while sending appnotification", e);
            }
        }
    }


    public enum NotificationType {

        EMAIL(e -> e.getTyp().getEmailPayload(e.getEventData()), new EmailNotificationSender()),
        TEAMS_NOTIFICATION(e -> e.getTyp().getTeamsNotificationPayload(e.getEventData()), new TeamsNotificationSender()),
        APP_NOTIFICATION(e -> e.getTyp().getAppNotificationPayload(e.getEventData()), new AppNotificationSender());

        private NotificationSender sender;
        private final EventToPayloadTransformer pg;

        NotificationType(EventToPayloadTransformer pg, NotificationSender sender) {
            this.pg = pg;
            this.sender = sender;
        }

        public NotificationPayLoad getPayload(Event e) {
            return pg.generate(e);
        }

        public NotificationSender getSender() {
            return this.sender;
        }
    }

    @With
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Notification {
        Object channelInfo;
        NotificationType typ;
        Event event;
    }

    private RecipientResolver getExternalEventRcptResolver() {
        return o -> ((List<String>)o.get("for")).stream()
                        .map(u -> Reciepient.builder()
                                    .id(UserServiceUtil.getGFromGtag(u) != null? UserServiceUtil.getGFromGtag(u) : UserServiceUtil.getUFromUtag(u))
                                    .typ(UserServiceUtil.getGFromGtag(u) != null? RecipientType.GROUP : RecipientType.USER)
                                    .build())
                        .collect(Collectors.toSet());
    }

    private RecipientResolver getNewCommentWatcherResolver() {
        return o -> {
            Set<Reciepient> rcpnts = new HashSet<>();
//            try {
//                SecurityUtil.setCurrentUser(o.getPublisher());
//                String comment = (new Gson()).fromJson((String) o.get("comment"), String.class);
//                Document d = DS.get((String) o.get("docid"));
//                rcpnts.add(new Reciepient().withId(d.getOwner()).withTyp(RecipientType.USER));
//                Matcher m = Pattern.compile("\\@([0-9A-Za-z]+)").matcher(comment);
//                while (m.find()) {
//                    String id = m.group(1);
//                    if (id.startsWith("g:")) {
//                        rcpnts.add(new Reciepient().withId(id.substring(2)).withTyp(RecipientType.GROUP));
//                    } else {
//                        rcpnts.add(new Reciepient().withId(id).withTyp(RecipientType.USER));
//                    }
//                }
//            } finally {
//                SecurityUtil.unsetCurrentUser();
//            }
            return rcpnts;
        };
    }

    private RecipientResolver getUserResolver() {
        return o -> Collections.singleton(Reciepient.builder().id(((User) o.get("user")).getUserName()).typ(RecipientType.USER).build());
    }

    private EventToEmailNotificationTransformer getExternalEventEmailBuilder() {
        return ed -> {
            if(ed.get("subject") == null)
                return null;
            return new EmailService.EmailObject()
                    .withSubject((String) ed.get("subject"))
                    .withBody((String) ed.get("txt"));
        };
    }

    private EventToAppNotificationTransfomer getExternalEventAppNotifBuilder() {
        return ed -> AppNotification.NotificationData.builder()
                .ptime(System.currentTimeMillis())
                .ntext((String) ed.get("txt"))
                .nFrom(ed.getPublisher())
                .build();
    }

    private EventToTeamsNotificationTransformer getExternalEventTeamsNotifBuilder() {
        return ed -> TeamsNotificationSenderService.TeamsNotification.builder()
                .body((String) ed.get("txt"))
                .build();
    }

    private EventToEmailNotificationTransformer getRegisterEmailTemplateBuilder() {
        return ed -> {
            User u = (User) ed.get("user");
            String url = String.format("%s/api/auth/validate/%s?tok=%s", App.getLocalAddr(), u.getUserName(), u.__getRegTok());
            return new EmailService.EmailObject()
                    .withSubject("Verify your account")
                    .withBody(
                            String.format(
                                    "Thanks for registering on inodes. <br/><br/><a href='%s'>Click here</a> to verify your inodes account, or open this url manually<br/>%s<br/><br/>Your username<br/>%s",
                                    url, url, u.getUserName()
                            )
                    );
        };
    }

    private EventToAppNotificationTransfomer getCommentAppNotificationBuilder() {
        return ed -> {
            String doc = (String) ed.get("docid");
            String txt = (String) ed.get("comment");
            return AppNotification.NotificationData.builder()
                    .nFrom("comments service")
                    .ntext(String.format(
                            "<a href=\"%s\"><b>%s</b></a> commented on <a href=\"%s\" target=\"_blank\">%s</a><br/>%s",
                            UrlUtil.getUserUrl(ed.getPublisher()), ed.getPublisher(), UrlUtil.getDocUrl(doc), doc, txt))
                    .ptime(System.currentTimeMillis())
                    .build();
        };
    }

    private EventToAppNotificationTransfomer getPermissionGivenAppNotificationBuilder() {
        return ed -> {
            String docId = (String) ed.get("docId");
            return AppNotification.NotificationData.builder()
                    .nFrom("permission service")
                    .ntext(String.format(
                            "Your permission request for <a href=\"%s\">this</a> object is approved. Try accessing it now",
                            UrlUtil.getDocUrl(docId)
                    ))
                    .ptime(System.currentTimeMillis())
                    .build();
        };
    }

    private EventToEmailNotificationTransformer getUserAddedToGroupEmailBuilder() {
        return ed -> {
            String adder = (String) ed.getPublisher();
            String group = (String) ed.get("group");
            return new EmailService.EmailObject()
                    .withSubject(String.format("%s added you to %s", adder, group))
                    .withBody(
                            String.format(
                                    "Congratualations! <a href='%s'>%s</a> added you to group <a href='%s'>%s</a>",
                                    UrlUtil.getUserUrl(adder), adder, UrlUtil.getGroupUrl(group), group
                            )
                    );
        };
    }

    private EventToEmailNotificationTransformer getNewCommentEmailBuilder() {
        return ed -> {
            String user = (String) ed.getPublisher();
            String id = (String) ed.get("docid");
            String comment = (String) ed.get("comment");

            return new EmailService.EmailObject()
                    .withSubject(user + " commented on your post")
                    .withBody(String.format(
                            "<div style='padding: 10px; border: solid 1px skyblue; display: block'>" +
                                    "<a href='%s'>%s</a> commented on your <a href='%s'>post</a></br>" +
                                    "<div style='padding: 10px; background-color: #f2f2f2; border: solid 1px gray; display: block'>%s</div>" +
                                    "</div>",
                            UrlUtil.getUserUrl(user), user, UrlUtil.getDocUrl(id), comment)
                    );
        };
    }

    public void post(Type typ, EventData content) {
        EQ.enqueue(Event.builder().typ(typ).eventData(content).build());
    }

    @PostConstruct
    public void init() {

        es = this;

        US.registerPostEvent(UserGroupService.Events.USER_ADDED_TO_GROUP, o -> post(EventService.Type.USER_ADD_TO_GROUP, o));
        CS.registerPostEvent(CollabService.EventType.NEW_COMMENT, o -> post(Type.NEW_COMMENT, o));

        /* start the event processing thread */
        new Thread(() -> {

            while (true) {

                try {

                    Event e = EQ.deque();

                    e.getTyp()
                        .getNotifTypes()
                        .forEach(notificationType -> {
                            try {

                                NotificationPayLoad payload = notificationType.getPayload(e);

                                if(payload == null)
                                    return;

                                Set<Reciepient> recipients = e.getReciepients();

                                notificationType.getSender().send(payload, recipients);

                            } catch (Exception ex) {
                                log.error("error while sending notification : " + e.toString(), ex);
                            }
                        });

                } catch (Throwable t) {
                    log.error("error in notification sender thread: ", t);
                }
            }
        }).start();
    }

}
