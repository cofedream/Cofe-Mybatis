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

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.service.TypeAliasService;

/**
 * @author : zhengrf
 * @date : 2019-09-20
 */
public class ClassTypeConverter extends Converter<PsiClass> implements CustomReferenceConverter<PsiClass> {

    @Override
    public PsiClass fromString(final String s, final ConvertContext context) {
        if (StringUtil.isEmptyOrSpaces(s)) {
            return null;
        }
        return TypeAliasService.getInstance(context.getProject()).getAliasPsiClass(s);
    }

    @Nullable
    @Override
    public String toString(@Nullable final PsiClass psiClass, final ConvertContext context) {
        return psiClass == null ? null : psiClass.getQualifiedName();
    }


    @NotNull
    @Override
    public PsiReference[] createReferences(final GenericDomValue<PsiClass> value, final PsiElement element, final ConvertContext context) {
        PsiClass psiClass = value.getValue();
        if (psiClass == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        return new PsiReference[] {PsiReferenceBase.createSelfReference(element, psiClass)};
    }
}
