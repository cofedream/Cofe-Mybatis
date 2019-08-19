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

package tk.cofe.plugin.mybatis.annotation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.StringUtils;

/**
 * Mapper xml 提示
 *
 * @author : zhengrf
 * @date : 2019-07-06
 */
public class MapperXmlAnnotator implements Annotator {

    private static final String CAN_NOT_FOUND_RESULTMAP = "Can not found ResultMap";
    private static final String CAN_NOT_FOUN_FIELD = "Can not found Field";
    private static final String MISSING_VALUE = "Missing value";

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {
        if (!(element instanceof XmlTag)) {
            return;
        }
        DomElement domElement = DomUtils.getDomElement(element);
        if (domElement instanceof PropertyAttribute) {
            processDomElement(holder, ((PropertyAttribute) domElement));
        }
        if (domElement instanceof ResultMapAttribute) {
            processDomElement(holder, ((ResultMapAttribute) domElement));
        }
    }

    private void processDomElement(@NotNull final AnnotationHolder holder, final ResultMapAttribute domElement) {
        process(holder, domElement.getResultMap().getXmlAttributeValue(), CAN_NOT_FOUND_RESULTMAP, domElement.getResultMapElement().isPresent());
    }

    private void processDomElement(@NotNull final AnnotationHolder holder, final PropertyAttribute attribute) {
        process(holder, attribute.getProperty().getXmlAttributeValue(), CAN_NOT_FOUN_FIELD, attribute.getPropertyField().isPresent());
    }

    private void process(@NotNull final AnnotationHolder holder, final XmlAttributeValue xmlAttributeValue, final String errorMessage, final boolean canResolve) {
        if (xmlAttributeValue == null) {
            return;
        }
        PsiElement targetElement = getPsiElement(xmlAttributeValue);
        if (targetElement == null) {
            holder.createErrorAnnotation(xmlAttributeValue, MISSING_VALUE);
            return;
        }
        if (!canResolve) {
            holder.createErrorAnnotation(targetElement, errorMessage);
        }
    }

    @Nullable
    private PsiElement getPsiElement(final XmlAttributeValue xmlAttributeValue) {
        for (PsiElement child : xmlAttributeValue.getChildren()) {
            if (StringUtils.isNotBlank(child.getText()) && !"\"".equals(child.getText())) {
                return child;
            }
        }
        return null;
    }
}
