package main.service.excel_parser.util;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean hasText(String str) {
        return str != null && !str.isEmpty() && containsText(str);
    }

    public static int getLastDigit(String str) {
        String lastDigit = str.replaceAll("\\D+", "");
        if (!lastDigit.isEmpty()) {
            return Integer.parseInt(lastDigit.substring(lastDigit.length() - 1));
        } else {
            return -1;
        }
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
