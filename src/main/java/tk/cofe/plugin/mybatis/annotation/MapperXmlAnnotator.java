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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.bundle.MyBatisBundle;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.Sql;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Delete;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Insert;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Select;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Update;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Mapper xml 提示,<a href="http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/annotator.html">详情</a>
 *
 * @author : zhengrf
 * @date : 2019-07-06
 */
public class MapperXmlAnnotator implements Annotator {

    private static final String MISSING_VALUE = "Missing value";

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {
        if (!(element instanceof XmlTag)) {
            return;
        }
        DomElement domElement = DomUtils.getDomElement(element);
        if (domElement instanceof IdAttribute) {
            processDomElement(holder, ((IdAttribute) domElement));
        }
    }

    private void processDomElement(final AnnotationHolder holder, final IdAttribute domElement) {
        String id = domElement.getIdValue().orElse(null);
        process(holder, domElement, domElement.getId().getXmlAttributeValue(), MyBatisBundle.message("xml.mapper.annotator.duplicate.text", "id", id), id);
    }

    private void process(@NotNull final AnnotationHolder holder, final IdAttribute domElement, final XmlAttributeValue xmlAttributeValue, final String errorMessage, final String id) {
        if (xmlAttributeValue == null || id == null) {
            return;
        }
        PsiElement targetElement = getPsiElement(xmlAttributeValue);
        if (targetElement == null) {
            holder.createErrorAnnotation(xmlAttributeValue, MISSING_VALUE);
            return;
        }
        Mapper mapper = DomUtils.getTargetElement(targetElement, Mapper.class);
        List<? extends IdAttribute> ids = getIdAttributes(domElement, mapper);
        if (ids.stream().filter(idInfo -> Objects.equals(id, idInfo.getIdValue().orElse(null))).count() > 1) {
            holder.createErrorAnnotation(targetElement, errorMessage);
        }
    }

    private List<? extends IdAttribute> getIdAttributes(final IdAttribute domElement, final Mapper mapper) {
        if (domElement instanceof Sql) {
            return mapper.getSqls();
        } else if (domElement instanceof Insert) {
            return mapper.getInserts();
        } else if (domElement instanceof Select) {
            return mapper.getSqls();
        } else if (domElement instanceof Update) {
            return mapper.getUpdates();
        } else if (domElement instanceof Delete) {
            return mapper.getDeletes();
        } else {
            return Collections.emptyList();
        }
    }

    @Nullable
    private PsiElement getPsiElement(final XmlAttributeValue xmlAttributeValue) {
        for (PsiElement child : xmlAttributeValue.getChildren()) {
            String text = child.getText();
            if (StringUtil.isNotEmpty(text)) {
                return child;
            }
        }
        return null;
    }
}
