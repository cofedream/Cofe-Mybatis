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
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.Optional;

/**
 * Mapper xml 提示
 *
 * @author : zhengrf
 * @date : 2019-07-06
 */
public class MapperXmlAnnotator implements Annotator {

    private static final String MESSAGE = "Can not found Method";

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {
        if (!(element instanceof XmlTag)) {
            return;
        }
        DomElement domElement = DomUtils.getDomElement(element);
        if (!(domElement instanceof ClassElement)) {
            return;
        }
        ClassElement classElement = (ClassElement) domElement;
        XmlAttributeValue xmlAttributeValue = classElement.getId().getXmlAttributeValue();
        if (xmlAttributeValue == null) {
            return;
        }
        PsiElement targetElement = getPsiElement(xmlAttributeValue);
        if (targetElement == null) {
            return;
        }
        if (StringUtils.isBlank(xmlAttributeValue.getValue())) {
            holder.createErrorAnnotation(targetElement, MESSAGE);
            return;
        }
        Optional<PsiMethod> psiMethod = JavaPsiService.getInstance(element.getProject()).findPsiMethod(classElement);
        if (!psiMethod.isPresent()) {
            holder.createErrorAnnotation(targetElement, MESSAGE);
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
