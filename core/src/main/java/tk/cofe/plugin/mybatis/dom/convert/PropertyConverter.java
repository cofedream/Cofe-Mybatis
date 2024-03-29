/*
 * Copyright (C) 2019-2023 cofe
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
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiModifier;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.bundle.MyBatisBundle;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.common.utils.PsiFieldUtils;
import tk.cofe.plugin.common.utils.PsiJavaUtils;
import tk.cofe.plugin.mybatis.dom.model.mix.IdOrResultMix;
import tk.cofe.plugin.mybatis.dom.model.tag.Association;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * property 属性
 *
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class PropertyConverter extends ResolvingConverter<PsiMember> {

    @Override
    public String getErrorMessage(@Nullable final String s, final ConvertContext context) {
        return MyBatisBundle.message("error.cannot.resolve.field.message", s);
    }

    @NotNull
    @Override
    public Collection<? extends PsiMember> getVariants(ConvertContext context) {
        return parse(context)
                .map(psiClass -> Arrays.stream(psiClass.getAllFields())
                        .filter(field -> !PsiFieldUtils.anyMatch(field, PsiModifier.FINAL, PsiModifier.STATIC))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Nullable
    @Override
    public LookupElement createLookupElement(PsiMember psiMember) {
        return Optional.ofNullable(psiMember)
                .map(NavigationItem::getName)
                .map(name -> LookupElementBuilder.create(name).withIcon(PsiJavaUtils.getPsiMemberIcon(psiMember)))
                .orElse(null);
    }

    @Nullable
    @Override
    public PsiMember fromString(@Nullable final String member, final ConvertContext context) {
        if (StringUtil.isEmpty(member)) {
            return null;
        }
        return parse(context)
                .map(psiClass -> psiClass.findFieldByName(member, true))
                .orElse(null);
    }

    @Nullable
    @Override
    public String toString(@Nullable final PsiMember psiMember, final ConvertContext context) {
        return Optional.ofNullable(psiMember)
                .map(NavigationItem::getName)
                .orElse(null);
    }

    public static Optional<PsiClass> parse(ConvertContext context) {
        final DomElement currentTagDomElement = context.getInvocationElement().getParent();
        // 当前属性对应的标签
        return parse(currentTagDomElement);
    }

    public static Optional<PsiClass> parse(final DomElement domElement) {
        if (domElement == null) {
            return Optional.empty();
        }
        final IdOrResultMix include = DomUtils.getParentOfType(domElement, IdOrResultMix.class);
        if (include instanceof ResultMap) {
            return ((ResultMap) include).getTypeValue();
        }
        if (include instanceof Association) {
            return ((Association) include).getJavaTypeValue();
        }
        if (include instanceof tk.cofe.plugin.mybatis.dom.model.tag.Collection) {
            return ((tk.cofe.plugin.mybatis.dom.model.tag.Collection) include).getOfTypeValue();
        }
        return Optional.empty();
    }
}
