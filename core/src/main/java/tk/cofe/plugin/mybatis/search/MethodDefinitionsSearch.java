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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.intellij.util.Processor;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.model.mix.CRUDMix;
import tk.cofe.plugin.mybatis.service.MapperService;

/**
 * Mapper 跳转 XML 标签定义 CTRL+ALT+B<br/>
 * 接口实现查找
 *
 * @author : zhengrf
 * @date : 2019-01-03
 */
public class MethodDefinitionsSearch extends QueryExecutorBase<PsiElement, DefinitionsScopedSearch.SearchParameters> {
    public MethodDefinitionsSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull DefinitionsScopedSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiElement> consumer) {
        PsiElement element = queryParameters.getElement();
        if (!(element instanceof PsiMethod)) {
            return;
        }
        MapperService.getInstance(element.getProject())
                .findStatement((PsiMethod) element)
                .map(CRUDMix::getId)
                .map(GenericAttributeValue::getXmlAttributeValue)
                .ifPresent(consumer::process);
    }
}