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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiType;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.utils.PsiMethodUtils;
import tk.cofe.plugin.common.utils.PsiTypeUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2021-03-06
 */
public class MblIdentifierReference extends PsiReferenceBase<PsiElement> {
    private final String prefixStr;
    private final PsiClass psiClass;
    private final PsiMethod psiMethod;

    public MblIdentifierReference(@NotNull PsiElement element, final TextRange rangeInElement, String prefixStr, PsiMethod psiMethod) {
        super(element, rangeInElement);
        this.psiMethod = psiMethod;
        final PsiType psiType = psiMethod.getReturnType();
        this.psiClass = PsiTypeUtils.isCustomType(psiType) ? ((PsiClassType) psiType).resolve() : null;
        this.prefixStr = prefixStr;
    }

    @Override
    public @Nullable PsiElement resolve() {
        return psiMethod;
    }

    @Override
    public Object @NotNull [] getVariants() {
        List<LookupElementBuilder> list = new ArrayList<>();
        for (PsiMember member : findPsiMember(psiClass).values()) {
            list.add(createLookup((PsiMethod) member));
        }
        // CompletionUtils.getPsiClassTypeVariants(psiClass,
        //         field -> {
        //             // list.add(creatLookup(field))
        //         },
        //         method -> list.add(creatLookup(method)));
        return list.toArray(LookupElementBuilder[]::new);
    }

    @Nonnull
    private LookupElementBuilder createLookup(PsiField psiField) {
        return LookupElementBuilder.create(psiField, prefixStr + psiField.getName())
                // .withTypeText(psiField.getName())
                // .withTailText(psiField.getName())
                .withPresentableText(psiField.getName())
                ;
    }

    @Nonnull
    private LookupElementBuilder createLookup(PsiMethod psiMethod) {
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
