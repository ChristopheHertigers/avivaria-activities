package be.indigosolutions.framework.util;

/**
 * User: christophe
 * Date: 08/11/13
 */
public class NumberUtils {
    public static boolean isDecimal(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((!Character.isDigit(str.charAt(i))) && (str.charAt(i) != '.')) {
                return false;
            }
        }
        return true;
    }
}
