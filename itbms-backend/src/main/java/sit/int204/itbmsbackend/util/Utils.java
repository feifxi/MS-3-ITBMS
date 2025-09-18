package sit.int204.itbmsbackend.util;

public class Utils {
    public static String trimOrSetNull(String text) {
        return text == null || text.trim().isEmpty() ? null : text.trim();
    }
}
