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
import com.intellij.psi.PsiElement;
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

    public static class Id extends ResolvingConverter.StringConverter {

        @NotNull
        @Override
        public Collection<? extends String> getVariants(ConvertContext context) {
            ClassElement classElement = DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class, true);
            if (classElement == null) {
                return Collections.emptyList();
            }
            return classElement.getIdValue().map(id -> JavaPsiService.getInstance(context.getProject()).findPsiClass(classElement)
                    .map(psiClass -> Arrays.stream(psiClass.getMethods())
                            .map(PsiMethod::getName)
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList()))
                    .orElse(Collections.emptyList());
        }

        @Nullable
        @Override
        public PsiElement resolve(String methodName, ConvertContext context) {
            if (StringUtils.isBlank(methodName)) {
                return null;
            }
            return JavaPsiService.getInstance(context.getProject())
                    .findPsiMethod(DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class, true))
                    .filter(psiMethod -> methodName.equals(psiMethod.getName()))
                    .orElse(null);
        }

        @Nullable
        @Override
        public LookupElement createLookupElement(String s) {
            return LookupElementBuilder.create(s).withIcon(AllIcons.Nodes.Method);
        }
    }
}
