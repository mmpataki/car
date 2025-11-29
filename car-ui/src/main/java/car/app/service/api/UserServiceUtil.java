package car.app.service.api;

import org.springframework.stereotype.Service;

@Service
public class UserServiceUtil extends Observable {

    public static String getUFromUtag(String uTag) {
        if (uTag.startsWith("u-")) return uTag.substring(2);
        return null;
    }

    public static String getGFromGtag(String gTag) {
        if (gTag.startsWith("g-")) return gTag.substring(2);
        return null;
    }

    public static String getUserTag(String userName) {
        return "u-" + userName;
    }

    public static String getGroupTag(String groupName) {
        return "g-" + groupName;
    }

}
