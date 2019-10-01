package uk.co.magictractor.spew.util;

/**
 * Other String utility methods are available in
 * {@link com.google.common.base.Strings}.
 */
public final class StringUtil {

    private StringUtil() {
    }

    /**
     * <p>
     * Convert camel case to words by inserting a space before capital letters,
     * other than the first letter.
     * </p>
     */
    public static String wordify(String str) {
        StringBuffer wordBuffer = new StringBuffer();
        int strLen = str.length();
        if (strLen == 0) {
            return str;
        }

        wordBuffer.append(str.charAt(0));

        for (int i = 1; i < strLen; i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                wordBuffer.append(' ');
            }
            wordBuffer.append(c);
        }

        return wordBuffer.toString();
    }

}
