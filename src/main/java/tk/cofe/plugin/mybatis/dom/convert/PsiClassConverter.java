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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.constants.Empty;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Select;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * PsiClass 相关转换
 *
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class PsiClassConverter {

    private static abstract class Base extends ResolvingConverter<PsiClass> {

        @Nullable
        @Override
        public PsiClass fromString(@Nullable String className, ConvertContext context) {
            return className != null ? JavaPsiService.getInstance(context.getProject()).findPsiClass(className).orElse(null) : null;
        }

        @Nullable
        @Override
        public String toString(@Nullable PsiClass psiClass, ConvertContext context) {
            return psiClass == null ? null : psiClass.getQualifiedName();
        }

        @Nullable
        @Override
        public LookupElement createLookupElement(PsiClass psiClass) {
            LookupElement lookupElement = PsiMybatisUtils.getResultTypeLookupElement(psiClass.getName());
            if (lookupElement != null) {
                return lookupElement;
            }
            return createLookupElementBuilder(psiClass.getQualifiedName(), psiClass.getQualifiedName(), psiClass.getName()).withIcon(AllIcons.Nodes.Class);
        }

        private static LookupElementBuilder createLookupElementBuilder(String lookupString, String typeText, String tailText) {
            return LookupElementBuilder.create(lookupString).withTypeText(typeText).withPresentableText(Empty.STRING).appendTailText(tailText, true);
        }

    }

    public static class ResultType extends Base {

        @NotNull
        @Override
        public Collection<? extends PsiClass> getVariants(ConvertContext context) {
            Select select = (Select) DomUtils.getDomElement(context.getTag());
            if (select == null) {
                return Collections.emptyList();
            }
            return select.getIdMethod().map(psiMethod -> {
                PsiType returnType = psiMethod.getReturnType();
                if (returnType instanceof PsiClassReferenceType) {
                    return Collections.singletonList(((PsiClassReferenceType) returnType).resolve());
                }
                return Collections.<PsiClass>emptyList();
            }).orElse(Collections.emptyList());
        }

    }

    public static class ParameterType extends Base {

        @NotNull
        @Override
        public Collection<? extends PsiClass> getVariants(ConvertContext context) {
            Select select = (Select) DomUtils.getDomElement(context.getTag());
            if (select == null) {
                return Collections.emptyList();
            }
            return select.getIdMethod().map(psiMethod -> {
                PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
                if (parameters.length != 1) {
                    return Collections.<PsiClass>emptyList();
                }
                PsiType type = parameters[0].getType();
                if (type instanceof PsiClassReferenceType) {
                    return Collections.singletonList(((PsiClassReferenceType) type).resolve());
                }
                return Collections.<PsiClass>emptyList();
            }).orElse(Collections.emptyList());
        }

    }

}
