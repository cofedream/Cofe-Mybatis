package tk.cofedream.plugin.mybatis.utils;

import java.util.Collection;

/**
 * @author : zhengrf
 * @date : 2019-01-04
 */
public class CollectionUtils {
    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }
}
