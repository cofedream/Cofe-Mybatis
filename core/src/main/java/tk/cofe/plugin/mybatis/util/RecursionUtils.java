package tk.cofe.plugin.mybatis.util;

import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Collection;
import tk.cofe.plugin.mybatis.dom.model.tag.Association;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 递归工具
 *
 * @author : zhengrf
 * @date : 2019-11-22
 */
public class RecursionUtils {

    public static List<ResultMapAttribute> recursionResultMapAttribute(@Nullable ResultMap resultMap) {
        if (resultMap == null) {
            return Collections.emptyList();
        }
        LinkedList<ResultMapAttribute> res = new LinkedList<>();
        res.addAll(recursionAssociation(resultMap.getAssociations()));
        res.addAll(recursionCollection(resultMap.getCollections()));
        return res;
    }

    public static List<? extends ResultMapAttribute> recursionAssociation(List<Association> associations) {
        if (CollectionUtils.isEmpty(associations)) {
            return Collections.emptyList();
        }
        List<ResultMapAttribute> res = new LinkedList<>(associations);
        for (Association association : associations) {
            res.addAll(recursionAssociation(association.getAssociations()));
            res.addAll(recursionCollection(association.getCollections()));
        }
        return res;
    }

    public static List<? extends ResultMapAttribute> recursionCollection(List<Collection> associations) {
        if (CollectionUtils.isEmpty(associations)) {
            return Collections.emptyList();
        }
        List<ResultMapAttribute> res = new LinkedList<>(associations);
        for (Collection collection : associations) {
            res.addAll(recursionAssociation(collection.getAssociations()));
            res.addAll(recursionCollection(collection.getCollections()));
        }
        return res;
    }

}
