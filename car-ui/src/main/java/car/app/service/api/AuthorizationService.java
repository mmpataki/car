package car.app.service.api;

import car.app.service.DatasetService;
import car.util.SecurityUtil;
import car.engine.processor.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorizationService {

    @Autowired
    UserGroupService AS;

    @Autowired
    UserGroupService GS;


    public void checkCommentPermission(String userId, String id) throws Exception {
        if (!AS.getUser(userId).getRoles().contains("COMMENT"))
            reject("comment on " + id);
    }

    public void checkCommentPermission(String id) throws Exception {
        checkCommentPermission(SecurityUtil.getCurrentUser(), id);
    }

    public void checkCommentDeletePermission(String userId, String id, String owner) throws Exception {
        if (!userId.equals(owner) || !AS.getUser(userId).getRoles().contains("COMMENT")) {
            reject(" delete comment from " + owner);
        }
    }

    public void checkCommentDeletePermission(String id, String owner) throws Exception {
        checkCommentDeletePermission(SecurityUtil.getCurrentUser(), id, owner);
    }

    public void checkGroupCreationPermissions() throws Exception {
        // logged in? then fine
    }

    public void checkAddUserToGroupPermission(String group) throws Exception {
        String user = SecurityUtil.getCurrentUser();
        if (!AS.isAdmin(user) && !GS.getGroup(group).getUsers().contains(user)) {
            reject("add users to " + group);
        }
    }

    public void checkDeleteUserFromGroupPermission(String group) throws Exception {
        String user = SecurityUtil.getCurrentUser();
        if (!AS.isAdmin(user) && !GS.getGroup(group).getUsers().contains(user)) {
            reject("delete users from " + group);
        }
    }

    private void reject(String action) throws UnAuthorizedException {
        throw new UnAuthorizedException(SecurityUtil.getCurrentUser() + " has no permission to " + action);
    }

    public void checkNotificationSendPermission(List<String> ugids) throws Exception {
        String cuid = SecurityUtil.getCurrentUser();
        for (String u : ugids) {
            String uid = UserServiceUtil.getUFromUtag(u);
            if (uid != null && AS.getUser(uid) != null) {
                continue;
            }
            String gid = UserServiceUtil.getGFromGtag(u);
            if (gid != null) {
                if (gid.equals(UserGroupService.PUBLIC)) {
                    if (!AS.amIAdmin() && !AS.getGroupsOf(cuid).contains(UserGroupService.SECURITY)) {
                        reject("send notifications to " + gid);
                    }
                } else {
                    if (!AS.getGroupsOf(cuid).contains(gid)) {
                        reject("send notifications to " + gid + " (" + cuid + " not in " + gid + ")");
                    }
                }
            }
        }
    }

    public void checkRuleCreatePermission() throws Exception {
        String user = SecurityUtil.getCurrentUser();
        if (!AS.isAdmin(user)) {
            reject("create a rule");
        }
    }

    public void checkRuleDeletePermission() throws Exception {
        String user = SecurityUtil.getCurrentUser();
        if (!AS.isAdmin(user)) {
            reject("delete a rule");
        }
    }

    public void checkCleanUpPermission(String dsetId) throws UnAuthorizedException {
        try {

        } catch (Exception e) {
            reject("cleanup a dataset [" + dsetId + "]");
        }
    }

    public void checkExtractPermission(String dsetId) throws UnAuthorizedException {
        try {

        } catch (Exception e) {
            reject("extract from a dataset [" + dsetId + "]");
        }
    }
}
