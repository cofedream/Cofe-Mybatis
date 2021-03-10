/*
 * Copyright (C) 2019-2021 cofe
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

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Bind;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Foreach;
import tk.cofe.plugin.mybatis.dom.model.include.BindInclude;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mybatis Mapper XML 相关工具
 *
 * @author : zhengrf
 * @date : 2021-03-10
 */
public class MybatisXMLUtils {

    /**
     * 获取父类中包含的所有 {@code <bind/>} 标签
     *
     * @param psiElement Psi元素
     */
    public static List<Bind> getTheBindTagInParents(PsiElement psiElement) {
        if (!(psiElement instanceof XmlElement)) {
            return Collections.emptyList();
        }
        return DomUtils.getParents(psiElement, XmlTag.class, BindInclude.class)
                .stream()
                .flatMap(info -> info.getBinds().stream())
                .collect(Collectors.toList());
    }

    /**
     * 获取父类中包含的所有 {@code <foreach/>} 标签
     *
     * @param psiElement Psi元素
     */
    public static List<Foreach> getTheForeachTagInParents(PsiElement psiElement) {
        if (!(psiElement instanceof XmlElement)) {
            return Collections.emptyList();
        }
        return DomUtils.getParents(psiElement, XmlTag.class, Foreach.class);
    }
}
