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

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.bundle.MyBatisBundle;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.common.utils.PsiMethodUtils;
import tk.cofe.plugin.mybatis.dom.model.mix.CRUDMix;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author : zhengrf
 * @date : 2019-01-23
 */
public class CRUDMixConverter {

    public static class Id extends ResolvingConverter<PsiMethod> {
        @Override
        public String getErrorMessage(@Nullable final String s, final ConvertContext context) {
            return MyBatisBundle.message("error.cannot.resolve.method.message", s);
        }

        @NotNull
        @Override
        public Collection<? extends PsiMethod> getVariants(ConvertContext context) {
            CRUDMix CRUDMix = DomUtils.getParentOfType(context.getInvocationElement(), CRUDMix.class);
            if (CRUDMix == null) {
                return Collections.emptyList();
            }
            return MybatisUtils.getPsiClass(CRUDMix)
                    .map(psiClass -> Arrays.asList(psiClass.getMethods()))
                    .orElse(Collections.emptyList());
        }

        @Nullable
        @Override
        public PsiMethod fromString(@Nullable final String methodName, final ConvertContext context) {
            if (StringUtil.isEmpty(methodName)) {
                return null;
            }
            CRUDMix CRUDMix = DomUtils.getParentOfType(context.getInvocationElement(), CRUDMix.class, true);
            if (CRUDMix == null) {
                return null;
            }
            return MybatisUtils.getPsiClass(CRUDMix).flatMap(psiClass -> PsiMethodUtils.findPsiMethod(psiClass, methodName)).orElse(null);
        }

        @Nullable
        @Override
        public String toString(@Nullable final PsiMethod psiMethod, final ConvertContext context) {
            return psiMethod == null ? null : psiMethod.getName();
        }

        @Nullable
        @Override
        public LookupElement createLookupElement(PsiMethod psiMethod) {
            return psiMethod == null ? null : LookupElementBuilder.create(psiMethod.getName()).withIcon(AllIcons.Nodes.Method);
        }
    }
}
