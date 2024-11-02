package be.indigosolutions.framework.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: christophe
 * Date: 08/11/13
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static boolean containsOnlyDecimalsAnd(String str, Character... characters) {
        Set<Character> chars = new HashSet<Character>(Arrays.asList(characters));
        chars.addAll(Arrays.asList('0','1','2','3','4','5','6','7','8','9','.',','));
        return onlyContains(str, chars.toArray(new Character[chars.size()]));
    }

    private static boolean onlyContains(String str, Character... characters) {
        if (isBlank(str)) return true;
        int sz = str.length();
        List<Character> chars = Arrays.asList(characters);
        for (int i = 0; i < sz; i++) {
            if (!chars.contains(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
