package de.bimalo.common;

/**
 * A utility class for manipulating Strings.
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public abstract class StringUtils {

    /**
     * Private constructor to avoid creating instances of this class.
     */
    private StringUtils() {
    }

    /**
     * Combine str1 and str2 to a new string.
     *
     * @param str1 first string
     * @param str2 second string
     * @return combined string
     */
    public static String concatString(String str1, String str2) {
        StringBuilder sb = new StringBuilder();
        if (str1 != null && !str1.isEmpty()) {
            sb.append(str1);
        }
        if (str2 != null && !str2.isEmpty()) {
            sb.append(str2);
        }
        return sb.toString();
    }
}
