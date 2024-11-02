package be.indigosolutions.framework.util;

import java.util.*;

/**
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 15-aug-2008
 */
public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {

    /**
     * Checks if the map is not <code>null</code> or empty.
     *
     * @param map the map to check.
     * @return true if the collection is not null or empty.
     */
    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    /**
     * Checks if the map is <code>null</code> or empty.
     *
     * @param map the map to check.
     * @return true if the collection is null or empty.
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /**
     * Pops the last element of the list and returns it.
     *
     * @param list the list
     * @return the last element of the list.
     */
    public static <T> T pop(List<T> list) {
        if (list.isEmpty()) return null;
        return list.remove(list.size() - 1);
    }

    /**
     * Shift the first element of the list and moves
     * all remaining elements to the front.
     *
     * @param list the list to shift
     * @return the first element that was on the list, or null if the list was empty.
     */
    public static <T> T shift(List<T> list) {
        Iterator<T> it = list.iterator();
        if (it.hasNext()) {
            T o = it.next();
            it.remove();
            return o;
        }
        return null;
    }

    /**
     * Creates a List from the given array.
     *
     * @param array the array to convert into a list
     * @return a list containing all the element of the array
     */
    public static <T> List<T> fromArray(T[] array) {
        List<T> result = new ArrayList<T>(array.length);
        result.addAll(Arrays.asList(array));
        return result;
    }

    /**
     * Joins all elements in a collection together in a string,
     * using the given separator.
     * <p/>
     * This is useful for printing collection for example,
     * of for passing collections in URLs.
     * <p/>
     * If the collection contains anything else than Strings or primitive wrappers,
     * the {@link Object#toString()} method will be called.
     *
     * @param collection the collection to join
     * @param separator  the separator to use
     * @return a string containing all elements of the collection, separated by
     *         the given separator.
     * @see #split(String,String);
     */
    public static String join(Collection collection, String separator) {
        Iterator it = collection.iterator();
        StringBuffer s = new StringBuffer();
        while (it.hasNext()) {
            s.append(it.next());
            if (it.hasNext()) s.append(separator);
        }
        return s.toString();
    }

    /**
     * Joins all elements in an array together in a string,
     * using the given separator.
     * <p/>
     * This is useful for printing collection for example,
     * of for passing collections in URLs.
     * <p/>
     * If the collection contains anything else than Strings or primitive wrappers,
     * the {@link Object#toString()} method will be called.
     *
     * @param array     the array to join
     * @param separator the separator to use
     * @return a string containing all elements of the collection, separated by
     *         the given separator.
     * @see #split(String,String);
     */
    public static String join(Object[] array, String separator) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            s.append(array[i]);
            if (i < array.length) s.append(separator);
        }
        return s.toString();
    }

    /**
     * Joins all elements in a collection together in a string,
     * using the given separator. In addition each value will be surrounded
     * with the prefix and suffix specified before being joined.
     * <p/>
     * This is useful for printing collection for example,
     * of for passing collections in URLs.
     * <p/>
     * If the collection contains anything else that Strings are primitive wrappers,
     * the {@link Object#toString()} method will be called.
     *
     * @param collection the collection to join
     * @param separator  the separator to use
     * @param prefix     prefix added to each value before joining
     * @param suffix     suffix added to each value before joining
     * @return a string containing all elements of the collection, separated by
     *         the given separator.
     * @see #split(String,String);
     */
    public static String join(Collection collection, String separator, String prefix, String suffix) {
        Iterator it = collection.iterator();
        StringBuffer s = new StringBuffer();
        while (it.hasNext()) {
            s.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) s.append(separator);
        }
        return s.toString();
    }

    /**
     * Splits a string into a list using the separator.
     * <p/>
     * This is useful for converting a string list representation
     * into a real {@link List}. This is effectively the reverse of
     * calling {@link #join(Collection,String)}.
     *
     * @param str       the string to split
     * @param separator the separator to split by
     * @return a list of the splitted string tokens.
     * @see #join(Collection,String)
     */
    public static List<String> split(String str, String separator) {
        StringTokenizer tok = new StringTokenizer(str, separator);
        List<String> result = new ArrayList<String>(tok.countTokens());
        while (tok.hasMoreElements()) {
            result.add((String) tok.nextElement());
        }
        return result;
    }

}
