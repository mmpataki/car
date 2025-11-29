package car.app.util;

import car.app.App;

public class UrlUtil {

    public static String getUserUrl(String userId) {
        return String.format("%s/?q=%%23inodesapp+%%23user+!%s", App.getLocalAddr(), userId);
    }

    public static String getGroupUrl(String groupId) {
        return String.format("%s/?q=%%23inodesapp+%%23viewgroup+!%s", App.getLocalAddr(), groupId);
    }

    public static String getDocUrl(String docId) {
        return String.format("%s/?q=@%s", App.getLocalAddr(), docId);
    }

    public static String getRelativeDocPermApprovalLink(String docId, String userId) {
        return String.format("/data/%s/givePermission/%s", docId, userId);
    }

}
