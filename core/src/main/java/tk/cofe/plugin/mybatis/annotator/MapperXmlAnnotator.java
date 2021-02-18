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

package tk.cofe.plugin.mybatis.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.bundle.MyBatisBundle;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Optional;

/**
 * Mapper xml 提示,<a href="https://plugins.jetbrains.com/docs/intellij/annotator.html">详情</a>
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

    private void process(final AnnotationHolder holder, final IdAttribute domElement, final String errorMessage, final String id) {
        Optional.ofNullable(DomUtils.getParentOfType(domElement, Mapper.class)).ifPresent(mapper -> {
            if (mapper.getIdElements(domElement).stream().filter(info -> info.isEqualsId(id)).count() > 1) {
                XmlElement element = DomUtils.getValueElement(domElement.getId());
                if (element != null) {
                    holder.newAnnotation(HighlightSeverity.ERROR, errorMessage)
                            .range(element)
                            .tooltip(errorMessage)
                            .create();
                }
            }
        });
    }

}
