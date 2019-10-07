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

package tk.cofe.plugin.mybatis.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.bundle.MyBatisBundle;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Sql;
import tk.cofe.plugin.mybatis.dom.model.tag.Delete;
import tk.cofe.plugin.mybatis.dom.model.tag.Insert;
import tk.cofe.plugin.mybatis.dom.model.tag.Select;
import tk.cofe.plugin.mybatis.dom.model.tag.Update;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Mapper xml 提示,<a href="http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/annotator.html">详情</a>
 *
 * @author : zhengrf
 * @date : 2019-07-06
 */
public class MapperXmlAnnotator implements Annotator {

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
        domElement.getIdValue().ifPresent(id -> process(holder, domElement, MyBatisBundle.message("xml.mapper.annotator.duplicate.text", "id", id), id));
    }

    private void process(@NotNull final AnnotationHolder holder, final IdAttribute domElement, final String errorMessage, final String id) {
        Optional.ofNullable(DomUtils.getParentOfType(domElement, Mapper.class)).ifPresent(mapper -> {
            if (getIdAttributes(domElement, mapper).stream().filter(info -> info.isEqualsId(id)).count() > 1) {
                XmlElement element = DomUtils.getValueElement(domElement.getId());
                if (element != null) {
                    holder.createErrorAnnotation(element, errorMessage);
                }
            }
        });
    }

    @NotNull
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

}