package tk.cofe.plugin.mybatis.util;

/**
 * @author : zhengrf
 * @date : 2019-01-08
 */
public final class StringUtils {

    /**
     * 字符串不为空
     * @param cs 字符串
     * @return {@code true} if the CharSequence is not empty and not null
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    /**
     * 字符串为空
     * @param cs 字符串
     * @return {@code true} if the CharSequence is null, empty or whitespace
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
