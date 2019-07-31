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

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2019-01-23
 */
public class ClassElementConverter {

    public static class Id extends ResolvingConverter<PsiMethod> {

        @NotNull
        @Override
        public Collection<? extends PsiMethod> getVariants(ConvertContext context) {
            ClassElement classElement = DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class, true);
            if (classElement == null) {
                return Collections.emptyList();
            }
            return JavaPsiService.getInstance(context.getProject()).findPsiClass(classElement)
                    .map(psiClass -> Arrays.stream(psiClass.getMethods())
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList());
        }

        @Nullable
        @Override
        public PsiMethod fromString(@Nullable final String methodName, final ConvertContext context) {
            if (StringUtils.isBlank(methodName)) {
                return null;
            }
            ClassElement classElement = DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class, true);
            if (classElement == null) {
                return null;
            }
            return classElement.getNamespaceValue()
                    .flatMap(classQualifiedName -> JavaPsiService.getInstance(context.getProject()).findPsiMethod(classQualifiedName, methodName))
                    .orElse(null);
        }

        @Nullable
        @Override
        public String toString(@Nullable final PsiMethod psiMethod, final ConvertContext context) {
            return psiMethod == null ? null : psiMethod.getName();
        }

        @Nullable
        @Override
        public LookupElement createLookupElement(PsiMethod psiMethod) {
            return LookupElementBuilder.create(psiMethod.getName()).withIcon(AllIcons.Nodes.Method);
        }
    }
}
