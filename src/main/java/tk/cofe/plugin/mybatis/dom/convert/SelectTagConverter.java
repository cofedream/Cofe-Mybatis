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
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.constants.Empty;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Select;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Select 标签相关转换
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class SelectTagConverter {

    public static class ResultType extends ResolvingConverter<String> {

        private static LookupElementBuilder createLookupElementBuilder(String lookupString, String typeText, String tailText) {
            return LookupElementBuilder.create(lookupString).withTypeText(typeText).withPresentableText(Empty.STRING).appendTailText(tailText, true);
        }

        @NotNull
        @Override
        public Collection<? extends String> getVariants(ConvertContext context) {
            return JavaPsiService.getInstance(context.getProject()).findPsiMethod((Select) DomUtils.getDomElement(context.getTag())).map(psiMethod -> PsiMybatisUtils.getResultType(psiMethod.getReturnType())).orElse(Collections.emptyList());
        }

        @Nullable
        @Override
        public String fromString(@Nullable String s, ConvertContext context) {
            return s;
        }

        @Nullable
        @Override
        public String toString(@Nullable String s, ConvertContext context) {
            return s;
        }

        @Nullable
        @Override
        public LookupElement createLookupElement(String text) {
            LookupElement lookupElement = PsiMybatisUtils.getResultTypeLookupElement(text);
            if (lookupElement != null) {
                return lookupElement;
            }
            String shortName = text;
            if (text.lastIndexOf(".") > 0) {
                shortName = text.substring(text.lastIndexOf(".") + 1);
            }
            return createLookupElementBuilder(text, text, shortName).withIcon(AllIcons.Nodes.Class);
        }
    }

}
