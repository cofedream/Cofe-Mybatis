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

package tk.cofe.plugin.mybatis.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MybatisGotoDeclarationHandler extends GotoDeclarationHandlerBase {
    @Nullable
    @Override
    public PsiElement getGotoDeclarationTarget(@Nullable PsiElement sourceElement, Editor editor) {
        if (!MybatisUtils.isElementWithMapperXMLFile(sourceElement)) {
            return null;
        }
        XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(sourceElement, XmlAttribute.class);
        return xmlAttribute == null ? null : parse(StatementAttribute.values(), xmlAttribute)
                .filter(statement -> sourceElement.getLanguage().is(XMLLanguage.INSTANCE) && MybatisUtils.isMapperXmlFile(sourceElement.getContainingFile()))
                .flatMap(statement -> statement.process(sourceElement)).orElse(null);
    }

    private enum StatementAttribute {
        ID("id") {
            @Override
            Optional<? extends PsiElement> process(PsiElement element) {
                return DomUtils.getDomElement(element, ClassElement.class).flatMap(ClassElement::getIdMethod);
            }
        },
        ;

        private String value;

        StatementAttribute(String attributeValue) {
            this.value = attributeValue;
        }

        abstract Optional<? extends PsiElement> process(PsiElement element);

        public String getValue() {
            return value;
        }
    }

    @NotNull
    private static Optional<StatementAttribute> parse(@NotNull StatementAttribute[] values, @NotNull XmlAttribute xmlAttribute) {
        for (StatementAttribute attribute : values) {
            if (attribute.getValue().equals(xmlAttribute.getName())) {
                return Optional.of(attribute);
            }
        }
        return Optional.empty();
    }
}
