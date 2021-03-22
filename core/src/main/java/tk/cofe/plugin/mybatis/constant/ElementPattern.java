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

import static com.intellij.patterns.PlatformPatterns.psiElement;

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
                .withText(StandardPatterns.string().startsWith("<foreach").endsWith("</foreach>"));
        public static final PsiElementPattern.Capture<XmlAttributeValue> COLLECTION_PATTERN = psiElement(XmlAttributeValue.class)
                .withParent(psiElement(XmlAttribute.class)
                        .withText(StandardPatterns.string().startsWith("collection"))
                        .withParent(FOREACH_PATTERN));
    }

}
