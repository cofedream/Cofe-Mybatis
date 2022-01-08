/*
 * Copyright (C) 2019-2022 cofe
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

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;

/**
 * @author : cofe
 * @date : 2022-01-08
 */
public class ResultMapReferencing implements CustomReferenceConverter<ResultMap> {
    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue<ResultMap> value, PsiElement element, ConvertContext context) {
        return new PsiReference[0];
    }
}
