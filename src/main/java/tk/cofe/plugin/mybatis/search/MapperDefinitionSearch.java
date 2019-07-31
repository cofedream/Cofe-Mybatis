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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTypeParameterListOwner;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
public class MapperDefinitionSearch extends QueryExecutorBase<XmlElement, PsiElement> {
    public MapperDefinitionSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull PsiElement queryParameters, @NotNull Processor<? super XmlElement> consumer) {
        if (!(queryParameters instanceof PsiTypeParameterListOwner)) {
            return;
        }
        //JavaPsiService.getInstance(queryParameters.getProject()).createStatement(queryParameters, (Processor<DomElement>) classElement -> consumer.createStatement(classElement.getXmlElement()));
    }
}
