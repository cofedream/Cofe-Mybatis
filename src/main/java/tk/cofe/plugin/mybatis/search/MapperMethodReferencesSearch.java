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

package tk.cofe.plugin.mybatis.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.psi.MapperReferenceContributor;
import tk.cofe.plugin.mybatis.psi.reference.IdAttributeReference;
import tk.cofe.plugin.mybatis.service.MapperService;

import java.util.Collection;

/**
 * 接口方法定位到XML标签 Ctrl+B
 * @author : zhengrf
 * @date : 2019-01-05
 * @see MapperReferenceContributor
 */
public class MapperMethodReferencesSearch extends QueryExecutorBase<PsiReference, com.intellij.psi.search.searches.MethodReferencesSearch.SearchParameters> {
    // 使用 DOM Converter 处理
    public MapperMethodReferencesSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull com.intellij.psi.search.searches.MethodReferencesSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiMethod method = queryParameters.getMethod();
        if (method.getContainingClass() != null) {
            Collection<Mapper> mappers = MapperService.getInstance(queryParameters.getProject()).findMapperXmls(method.getContainingClass());
            mappers.forEach(mapperXml -> mapperXml.getClassElements().forEach(element -> {
                element.getIdMethod().ifPresent(psiMethod -> {
                    XmlElement xmlElement = element.getXmlElement();
                    if (xmlElement instanceof XmlTag) {
                        XmlAttribute id = ((XmlTag) xmlElement).getAttribute("id");
                        if (id == null) {
                            return;
                        }
                        XmlAttributeValue valueElement = id.getValueElement();
                        if (valueElement != null) {
                            consumer.process(new IdAttributeReference(valueElement));
                        }
                    }
                });
            }));
        }
    }
}
