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

package tk.cofe.plugin.mbl.psi;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.utils.PsiMethodUtils;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author : zhengrf
 * @date : 2021-03-06
 */
public class MblDotReference extends PsiReferenceBase<PsiElement> {
    private final String prefixStr;
    private final PsiClass psiClass;

    public MblDotReference(@NotNull PsiElement element, TextRange rangeInElement, PsiClass psiClass, String prefixStr) {
        super(element, rangeInElement, true);
        this.psiClass = psiClass;
        this.prefixStr = prefixStr;
    }

    @Override
    public @Nullable PsiElement resolve() {
        return null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        List<LookupElementBuilder> list = new ArrayList<>();
        for (PsiMember member : findPsiMember(psiClass).values()) {
            list.add(creatLookup((PsiMethod) member));
        }
        // CompletionUtils.getPsiClassTypeVariants(psiClass,
        //         field -> {
        //             // list.add(creatLookup(field))
        //         },
        //         method -> list.add(creatLookup(method)));
        return list.toArray(LookupElementBuilder[]::new);
    }

    @Nonnull
    private LookupElementBuilder creatLookup(PsiField psiField) {
        return LookupElementBuilder.create(psiField, prefixStr + psiField.getName())
                // .withTypeText(psiField.getName())
                // .withTailText(psiField.getName())
                .withPresentableText(psiField.getName())
                ;
    }

    @Nonnull
    private LookupElementBuilder creatLookup(PsiMethod psiMethod) {
        String methodName = PsiMethodUtils.replaceGetPrefix(psiMethod);
        return LookupElementBuilder.create(psiMethod, prefixStr + methodName)
                // .withTypeText(psiMethod.getName())
                .withIcon(PlatformIcons.METHOD_ICON)
                .withPresentableText(methodName)
                .withTailText(psiMethod.getName())
                .withTypeText(Optional.ofNullable(psiMethod.getReturnType()).map(PsiType::getPresentableText).orElse(""))
                ;
    }

    public static Map<String, PsiMember> findPsiMember(PsiClass psiClass) {
        if (psiClass == null) {
            return Collections.emptyMap();
        }
        Map<String, PsiMember> res = new HashMap<>();
        for (PsiMethod method : psiClass.getMethods()) {
            if (method.isConstructor()
                    || PsiMethodUtils.isVoidMethod(method)
                    || PsiMethodUtils.isSetMethod(method)) {
                continue;
            }
            res.putIfAbsent(PsiMethodUtils.replaceGetPrefix(method), method);
        }
        res.putAll(findPsiMember(psiClass.getSuperClass()));
        return res;
    }
}
