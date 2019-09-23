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

package tk.cofe.plugin.mybatis.referencessearch;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.psi.MapperReferenceContributor;
import tk.cofe.plugin.mybatis.dom.psi.reference.PsiIdentifierReference;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.Optional;

/**
 * 从 XML标签定位到接口方法<br/>
 * 仅可引用，如果要重命名则需另外编写
 *
 * @author : zhengrf
 * @date : 2019-01-05
 * @see MapperReferenceContributor
 * @deprecated 使用 {@link MapperReferenceContributor}
 */
@Deprecated
public class MapperXmlReferencesSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    public MapperXmlReferencesSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement elementToSearch = queryParameters.getElementToSearch();
        if (!elementToSearch.getLanguage().is(XMLLanguage.INSTANCE)) {
            return;
        }
        if (!MybatisUtils.isMapperXmlFile(elementToSearch.getContainingFile())) {
            return;
        }
        XmlTag tag = PsiTreeUtil.getParentOfType(elementToSearch.getContext(), XmlTag.class, true);
        if (MybatisUtils.isBaseStatement(tag)) {
            Optional.ofNullable((ClassElement) DomUtils.getDomElement(tag)).ifPresent(classElement -> {
                classElement.getIdMethod().ifPresent(method -> {
                    PsiIdentifier nameIdentifier = method.getNameIdentifier();
                    if (nameIdentifier != null) {
                        consumer.process(new PsiIdentifierReference(nameIdentifier));
                    }
                });
            });
        }
    }
}
