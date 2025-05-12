package sit.int204.itbmsbackend.utils;

public class Utils {
    public static String validateString(String text) {
        return text == null || text.trim().isEmpty() ? null : text.trim();
    }
}
