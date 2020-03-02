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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.psi.reference.XmlAttributeValueReference;
import tk.cofe.plugin.mybatis.service.MapperService;

import java.util.List;
import java.util.Objects;

/**
 * @author : zhengrf
 * @date : 2019-01-23
 */
public class FieldReferenceSearch extends QueryExecutorBase<XmlAttributeValueReference, ReferencesSearch.SearchParameters> {

    public FieldReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Processor<? super XmlAttributeValueReference> consumer) {
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
                .filter(resultMap -> resultMap.getTypeValue().map(type -> isTarget(type, classQualifiedName)).orElse(false))
                .map(ResultMap::getPropertyAttributes)
                .forEach(attributes -> process(attributes, psiField, consumer));
    }

    private void process(List<? extends PropertyAttribute> attributes, PsiField psiField, Processor<? super XmlAttributeValueReference> consumer) {
        attributes.forEach(id -> {
            XmlAttributeValue xmlAttributeValue = id.getProperty().getXmlAttributeValue();
            if (xmlAttributeValue != null && Objects.equals(psiField.getName(), xmlAttributeValue.getValue())) {
                consumer.process(new XmlAttributeValueReference(xmlAttributeValue));
            }
        });
    }

    private boolean isTarget(@Nullable PsiClass targetClass, @NotNull String currentClassQualifiedName) {
        if (targetClass == null) {
            return false;
        }
        if (currentClassQualifiedName.equals(targetClass.getQualifiedName())) {
            return true;
        }
        if ("java.lang.Object".equals(targetClass.getQualifiedName())) {
            return false;
        }
        return isTarget(targetClass.getSuperClass(), currentClassQualifiedName);
    }
}
