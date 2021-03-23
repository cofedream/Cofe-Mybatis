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

package tk.cofe.plugin.mybatis.constant;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import tk.cofe.plugin.mbel.psi.MbELJdbcTypeConfig;
import tk.cofe.plugin.mbel.psi.MbELModeConfig;
import tk.cofe.plugin.mbel.psi.MbELReferenceExpression;
import tk.cofe.plugin.mbel.psi.MbELResultMapConfig;
import tk.cofe.plugin.mognl.psi.MOgnlReferenceExpression;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ResultTypeAttribute;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;
import static com.intellij.patterns.StandardPatterns.string;

/**
 * @author : zhengrf
 * @date : 2021-03-19
 */
public final class ElementPattern {

    public static class MOgnl {
        public static final PsiElementPattern.Capture<PsiElement> REFERENCE_EXPRESSION = psiElement().inside(MOgnlReferenceExpression.class);
    }

    public static class MbEL {
        public static final PsiElementPattern.Capture<PsiElement> REFERENCE_EXPRESSION = psiElement().inside(MbELReferenceExpression.class);

        //
        public static final PsiElementPattern.Capture<PsiElement> MODE_EXPRESSION = psiElement().inside(MbELModeConfig.class);
        public static final PsiElementPattern.Capture<PsiElement> JDBC_TYPE_EXPRESSION = psiElement().inside(MbELJdbcTypeConfig.class);
        public static final PsiElementPattern.Capture<PsiElement> RESULT_MAP_EXPRESSION = psiElement().inside(MbELResultMapConfig.class);
        //
        public static final PsiElementPattern.Capture<PsiElement> PARAM_CONFIG_EXPRESSION = psiElement().afterLeafSkipping(psiElement(MbELReferenceExpression.class), psiElement().withText(","));
    }

    public static class XML {

        /**
         * 匹配 foreach 标签
         *
         * @see tk.cofe.plugin.mybatis.dom.model.dynamic.Foreach#TAG
         */
        public static final PsiElementPattern.Capture<XmlTag> FOREACH_PATTERN = psiElement(XmlTag.class)
                .withText(or(string().startsWith("<foreach").endsWith("/>"), string().startsWith("<foreach").endsWith("</foreach>")));
        /**
         * 匹配 select 标签
         */
        public static final PsiElementPattern.Capture<XmlTag> SELECT_PATTERN = psiElement(XmlTag.class)
                .withText(or(string().startsWith("<select").endsWith("/>"), string().startsWith("<select").endsWith("</select>")));
        /**
         * 匹配 selectKey 标签
         */
        public static final PsiElementPattern.Capture<XmlTag> SELECT_KEY_PATTERN = psiElement(XmlTag.class)
                .withText(or(string().startsWith("<selectKey").endsWith("/>"), string().startsWith("<selectKey").endsWith("</selectKey>")));
        /**
         * 匹配 case 标签
         */
        public static final PsiElementPattern.Capture<XmlTag> CASE_PATTERN = psiElement(XmlTag.class)
                .withText(or(string().startsWith("<case").endsWith("/>"), string().startsWith("<case").endsWith("</case>")));

        public static final PsiElementPattern.Capture<XmlAttributeValue> COLLECTION_PATTERN = psiElement(XmlAttributeValue.class)
                .withParent(psiElement(XmlAttribute.class)
                        .withText(StandardPatterns.string().startsWith("collection"))
                        .withParent(FOREACH_PATTERN));
        /**
         * resultType
         */
        private static final PsiElementPattern.Capture<XmlAttribute> RESULT_TYPE_ATTRIBUTE = psiElement(XmlAttribute.class)
                .withText(StandardPatterns.string().startsWith("resultType"));
        /**
         * resultTypeValue
         *
         * @see ResultTypeAttribute
         */
        public static final PsiElementPattern.Capture<XmlAttributeValue> RESULT_TYPE_PATTERN = psiElement(XmlAttributeValue.class)
                .withParent(or(RESULT_TYPE_ATTRIBUTE.withParent(XML.SELECT_PATTERN),
                        RESULT_TYPE_ATTRIBUTE.withParent(XML.SELECT_KEY_PATTERN),
                        RESULT_TYPE_ATTRIBUTE.withParent(XML.CASE_PATTERN)));
    }

}
