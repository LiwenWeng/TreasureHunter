import java.util.Map;
import java.util.HashMap;

public class Colors {
    public static final String RESET = "\033[0m"; // Reset

    public static final Map<String, String> COLORS = new HashMap<>();
    static {
        COLORS.put("Black", "\033[0;30m");
        COLORS.put("Red", "\033[0;31m");
        COLORS.put("Green", "\033[0;32m");
        COLORS.put("Yellow", "\033[0;33m");
        COLORS.put("Blue", "\033[0;34m");
        COLORS.put("Purple", "\033[0;35m");
        COLORS.put("Cyan", "\033[0;36m");
        COLORS.put("White", "\033[0;37m");
    }

    public static String color(String str, String color) {
        return COLORS.get(color) + str + RESET;
    }
}