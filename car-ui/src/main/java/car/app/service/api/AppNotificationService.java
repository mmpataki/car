package car.app.service.api;

import car.app.models.AppNotification;
import car.app.repository.AppNotificationRepo;
import car.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class AppNotificationService {

    @Autowired
    AppNotificationRepo ANR;

    @Autowired
    UserGroupService UGS;

    @Autowired
    AuthorizationService AS;

    final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public List<AppNotification> getAllNotifs() {
        return StreamSupport.stream(ANR.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public List<AppNotification> getNotificationsFor(String userId, int start, int size) throws Exception {
        List<String> grps = new ArrayList<>(UGS.getGroupsOf(userId)).stream().map(UserServiceUtil::getGroupTag).collect(Collectors.toList());
        if (userId != null)
            grps.add(UserServiceUtil.getUserTag(userId));
        return ANR.findByNForInOrderByData_PtimeDesc(grps, PageRequest.of(start, size));
    }

    public void postNotification(List<String> ugids, AppNotification.NotificationData ndata) throws Exception {
        AS.checkNotificationSendPermission(ugids);
        ANR.saveAll(ugids.stream().map(u -> AppNotification.builder().NFor(u).data(ndata).seen(false).build()).collect(Collectors.toList()));
        ugids.stream().flatMap(ugid -> {
            String id = UserServiceUtil.getGFromGtag(ugid);
            if (id != null) {
                try {
                    return UGS.getGroup(UserServiceUtil.getGFromGtag(ugid)).getUsers().stream();
                } catch (Exception e) {
                    return Collections.EMPTY_LIST.stream();
                }
            } else {
                return Collections.singleton(UserServiceUtil.getUFromUtag(ugid)).stream();
            }
        }).forEach(u -> {
            List<SseEmitter> emitterList = emitters.get(u);
            if (emitterList != null) {
                emitterList.forEach(emitter -> {
                    try {
                        emitter.send(SseEmitter.event().data(ndata, MediaType.APPLICATION_JSON));
                    } catch (IOException e) {
                        log.debug("error while sending notification to " + emitter);
                        emitterList.remove(u);
                        }
                });
                if (emitterList.isEmpty())
                    emitters.remove(u);
            }
        });
    }

    public void markAsSeen(Long id) {
        AppNotification notification = ANR.findById(id).get();
        notification.setSeen(true);
        ANR.save(notification);
    }

    public Integer getUnseenNotificationCount() throws Exception {
        String userId = SecurityUtil.getCurrentUser();
        List<String> grps = new ArrayList<>(UGS.getGroupsOf(userId)).stream().map(UserServiceUtil::getGroupTag).collect(Collectors.toList());
        if (userId != null)
            grps.add(UserServiceUtil.getUserTag(userId));
        return ANR.countByNForInAndSeenFalse(grps);
    }

    public SseEmitter userLoggedIn(String user) {
        SseEmitter emitter = new SseEmitter();
        emitters.computeIfAbsent(user, s -> new LinkedList<>());
        emitters.get(user).add(emitter);
        emitter.onTimeout(() -> emitters.get(user).remove(emitter));
        emitter.onCompletion(() -> emitters.get(user).remove(emitter));
        return emitter;
    }

}
