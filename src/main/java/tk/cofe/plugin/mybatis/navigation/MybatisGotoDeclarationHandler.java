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
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.enums.AttributeEnums;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.EnumUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MybatisGotoDeclarationHandler extends GotoDeclarationHandlerBase {
    @Nullable
    @Override
    public PsiElement getGotoDeclarationTarget(@Nullable PsiElement sourceElement, Editor editor) {
        if (!PsiMybatisUtils.isElementWithMapperXMLFile(sourceElement)) {
            return null;
        }
        XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(sourceElement, XmlAttribute.class);
        return xmlAttribute == null ? null : EnumUtils.parse(StatementAttribute.values(), xmlAttribute).map(statement -> {
            if (sourceElement.getLanguage().is(XMLLanguage.INSTANCE)) {
                if (PsiMybatisUtils.isMapperXmlFile(sourceElement.getContainingFile())) {
                    return statement.process(sourceElement).orElse(null);
                }
            }
            return null;
        }).orElse(null);
    }

    private enum StatementAttribute implements AttributeEnums {
        ID("id") {
            @Override
            Optional<? extends PsiElement> process(PsiElement element) {
                return Optional.ofNullable(DomUtils.findDomElement(element, ClassElement.class)).map(ClassElement::getIdMethod).orElse(null);
            }
        },
        ;

        private String value;

        StatementAttribute(String attributeValue) {
            this.value = attributeValue;
        }

        @NotNull
        public static Optional<StatementAttribute> parse(@NotNull XmlAttribute xmlAttribute) {
            for (StatementAttribute attribute : values()) {
                if (attribute.getValue().equals(xmlAttribute.getName())) {
                    return Optional.of(attribute);
                }
            }
            return Optional.empty();
        }

        abstract Optional<? extends PsiElement> process(PsiElement element);

        public String getValue() {
            return value;
        }
    }
}
