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

package tk.cofe.plugin.mybatis.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.utils.PsiJavaUtils;
import tk.cofe.plugin.mybatis.dom.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.model.tag.Association;
import tk.cofe.plugin.mybatis.dom.model.tag.dynamic.Collection;
import tk.cofe.plugin.mybatis.service.MapperService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-23
 */
public class FieldReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

    public FieldReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement fieldElement = queryParameters.getElementToSearch();
        if (!(fieldElement instanceof PsiField)) {
            return;
        }
        PsiField psiField = (PsiField) fieldElement;
        PsiClass psiClass = psiField.getContainingClass();
        if (psiClass == null) {
            return;
        }
        String classQualifiedName = psiClass.getQualifiedName();
        if (StringUtil.isEmpty(classQualifiedName)) {
            return;
        }
        MapperService.getInstance(queryParameters.getProject()).getMapperStream()
                .flatMap(mapper -> mapper.getResultMaps().stream())
                .forEach(resultMap -> {
                    if (resultMap.getTypeValue().map(type -> isTarget(type, classQualifiedName)).orElse(false)) {
                        process(resultMap.getPropertyAttributes(), psiField, consumer);
                    }
                    process(resultMap.getAssociations(), resultMap.getCollections(), classQualifiedName, psiField, consumer);
                });
    }

    private void process(List<Association> associations, List<Collection> collections, String classQualifiedName, PsiField psiField, Processor<? super PsiReference> consumer) {
        for (Association association : associations) {
            if (association.getJavaTypeValue().map(type -> isTarget(type, classQualifiedName)).orElse(false)) {
                process(association.getPropertyAttributes(), psiField, consumer);
            }
            process(association.getAssociations(), association.getCollections(), classQualifiedName, psiField, consumer);
        }
        for (Collection collection : collections) {
            if (collection.getOfTypeValue().map(type -> isTarget(type, classQualifiedName)).orElse(false)) {
                process(collection.getPropertyAttributes(), psiField, consumer);
            }
            process(collection.getAssociations(), collection.getCollections(), classQualifiedName, psiField, consumer);
        }
    }

    private void process(List<? extends PropertyAttribute> attributes, PsiField psiField, Processor<? super PsiReference> consumer) {
        final String fieldName = psiField.getName();
        for (PropertyAttribute id : attributes) {
            Optional.ofNullable(id.getProperty())
                    .map(GenericAttributeValue::getXmlAttributeValue)
                    .filter(val -> Objects.equals(fieldName, val.getValue()))
                    .ifPresent(val -> consumer.process(new PsiReferenceBase<>(val) {
                        @Override
                        public @NotNull PsiElement resolve() {
                            return psiField;
                        }
                    }));
        }
    }

    private boolean isTarget(PsiClass targetClass, String currentClassQualifiedName) {
        if (targetClass == null) {
            return false;
        }
        if (PsiJavaUtils.isObjectClass(targetClass)) {
            return false;
        }
        if (currentClassQualifiedName.equals(targetClass.getQualifiedName())) {
            return true;
        }
        return isTarget(targetClass.getSuperClass(), currentClassQualifiedName);
    }
}
