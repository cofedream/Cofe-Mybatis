/*
 * Copyright (C) 2019 cofe
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mybatis.psi;

import com.intellij.patterns.XmlNamedElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.patterns.XmlTagPattern;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.psi.providers.ClassElementReferenceProvider;

/**
 * 注册 Xml Attribute 引用<br/>
 * 直接支持重命名
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MapperReferenceContributor extends PsiReferenceContributor {
    /**
     * "select", "update", "delete", "insert" 标签
     */
    private static final XmlTagPattern.Capture CLASS_ELEMENT_PATTERN = XmlPatterns.xmlTag().withLocalName("select", "update", "delete", "insert");

    private static final XmlNamedElementPattern.XmlAttributePattern ID_ATTRIBUTE = XmlPatterns.xmlAttribute("id");
    /**
     * "resultMap" 标签
     */
    private static final XmlTagPattern.Capture RESULTMAP = XmlPatterns.xmlTag().withLocalName("resultMap");
    //private static final XmlNamedElementPattern.XmlAttributePattern RESULT_TYPE = XmlPatterns.xmlAttribute().withLocalName("resultType");

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        //registrar.registerReferenceProvider(XML_ATTRIBUTE_VALUE_PATTERN, new XmlAttributeValueReferenceProvider());
        registrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue()
                        .withParent(ID_ATTRIBUTE
                                .withParent(CLASS_ELEMENT_PATTERN))
                , new ClassElementReferenceProvider.Id());
        // <include refid=""></include> 标签匹配
        //registrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue()
        //        .withParent(XmlPatterns.xmlAttribute("refid")
        //                .withParent(XmlPatterns.xmlTag().withLocalName("include"))), new IncludeTagReferenceProvider());
        // <result extends=""> 匹配
        //registrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue()
        //        .withParent(XmlPatterns.xmlAttribute("extends")
        //                .withParent(RESULTMAP)), new ResultMapReferenceProvider.Extends());
    }

}
