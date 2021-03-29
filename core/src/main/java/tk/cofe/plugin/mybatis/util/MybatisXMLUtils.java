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

package tk.cofe.plugin.mybatis.util;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.mybatis.dom.model.mix.BindMix;
import tk.cofe.plugin.mybatis.dom.model.tag.dynamic.Bind;
import tk.cofe.plugin.mybatis.dom.model.tag.dynamic.Foreach;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mybatis Mapper XML 相关工具
 *
 * @author : zhengrf
 * @date : 2021-03-10
 */
public class MybatisXMLUtils {

    /**
     * 获取父类中包含的所有 {@link Bind#TAG} 标签
     *
     * @param psiElement Psi元素
     */
    public static List<Bind> getTheBindTagInParents(PsiElement psiElement) {
        if (!(psiElement instanceof XmlElement)) {
            return Collections.emptyList();
        }
        return DomUtils.getParents(psiElement, XmlTag.class, BindMix.class)
                .stream()
                .flatMap(info -> info.getBinds().stream())
                .collect(Collectors.toList());
    }

    /**
     * 获取父类中包含的所有 {@link Foreach#TAG} 标签
     *
     * @param psiElement Psi元素
     */
    public static List<Foreach> getTheForeachTagInParents(PsiElement psiElement) {
        if (!(psiElement instanceof XmlElement)) {
            return Collections.emptyList();
        }
        return DomUtils.getParents(psiElement, XmlTag.class, Foreach.class);
    }

    public static PsiLanguageInjectionHost getOriginElement(final PsiElement element) {
        return InjectedLanguageManager.getInstance(element.getProject()).getInjectionHost(element);
    }
    public static PsiLanguageInjectionHost getOriginElement(final CompletionParameters parameters) {
        return getOriginElement(parameters.getPosition());
    }
}
