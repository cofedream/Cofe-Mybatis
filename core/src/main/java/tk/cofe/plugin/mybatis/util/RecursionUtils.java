/*
 * Copyright (C) 2019 cofe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mybatis.util;

import org.apache.commons.collections.CollectionUtils;
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

    public static List<ResultMapAttribute> recursionResultMapAttribute(ResultMap resultMap) {
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
