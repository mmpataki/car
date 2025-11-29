package car.util;

public class SecurityUtil {

    private static ThreadLocal<String> user = new ThreadLocal<>();

    public static String getCurrentUser() {
        return user.get();
    }

    public static void setCurrentUser(String value) {
        if (value != null)
            user.set(value);
    }

    public static void unsetCurrentUser() {
        user.remove();
    }

}
