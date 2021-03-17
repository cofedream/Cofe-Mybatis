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

package tk.cofe.plugin.mybatis.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.utils.*;
import tk.cofe.plugin.mognl.MOgnlTypes;
import tk.cofe.plugin.mybatis.psi.IdentifierReference;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhengrf
 * @date : 2020-01-19
 */
public class MOgnlReferenceProvider extends ReferenceExpressionReferenceProvider {

    @Override
    protected boolean isDOTElement(PsiElement element) {
        return MOgnlTypes.DOT == PsiElementUtils.getElementType(element);
    }

    @Nonnull
    @Override
    protected IdentifierReference createReference(@Nonnull PsiElement element, PsiType psiType, PsiMember suffixElement, TextRange textRange) {
        return new IdentifierReference(element, textRange, suffixElement, psiType) {
            @Override
            protected Collection<PsiMember> getClassMember() {
                return getTheMethodAndField(psiClass).values();
            }
        };
    }

    @Override
    protected PsiMember findSuffixElement(String name, PsiType psiType) {
        if (!(psiType instanceof PsiClassType)) {
            return null;
        }
        final PsiClass psiClass = ((PsiClassType) psiType).resolve();
        if (psiClass == null) {
            return null;
        }
        return CompletionUtils.getTheMethodOrField(psiClass,
                method -> name.equals(method.getName()) || name.equals(PsiMethodUtils.replaceGetPrefix(method)),
                field -> name.equals(field.getName()),
                field -> field, method -> method);
    }

    private Map<String, PsiMember> getTheMethodAndField(@Nullable final PsiClass psiClass) {
        if (psiClass == null) {
            return Collections.emptyMap();
        }
        if (PsiJavaUtils.isObjectClass(psiClass)) {
            return Collections.emptyMap();
        }
        Map<String, PsiMember> res = new HashMap<>();
        for (PsiMethod method : psiClass.getMethods()) {
            if (method.isConstructor()
                    || !PsiMethodUtils.isPublicMethod(method)
                    || PsiMethodUtils.isVoidMethod(method)) {
                // 是构造函数、非公共函数、没有返回值
                // 则不进行提示
                continue;
            }
            String methodName = method.getName();
            if (PsiMethodUtils.nameStartWithGet(method)) {
                methodName = PsiMethodUtils.replaceGetPrefix(method);
            } else if (PsiMethodUtils.nameStartWithIs(method)) {
                methodName = PsiMethodUtils.replaceIsPrefix(method);
            }
            res.putIfAbsent(methodName, method);
        }
        for (PsiField field : psiClass.getFields()) {
            if (PsiFieldUtils.notSerialField(field)) {
                res.putIfAbsent(field.getName(), field);
            }
        }
        res.putAll(getTheMethodAndField(psiClass.getSuperClass()));
        return res;
    }

}
