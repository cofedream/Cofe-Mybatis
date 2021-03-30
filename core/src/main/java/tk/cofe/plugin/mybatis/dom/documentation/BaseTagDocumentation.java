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

package tk.cofe.plugin.mybatis.dom.documentation;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomTarget;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.utils.DomUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-08-11
 */
abstract class BaseTagDocumentation<T extends DomElement> implements DocumentationProvider {
    @Nullable
    @Override
    public String getQuickNavigateInfo(final PsiElement element, final PsiElement originalElement) {
        return null;
    }

    @Nullable
    @Override
    public List<String> getUrlFor(final PsiElement element, final PsiElement originalElement) {
        return null;
    }

    @Nullable
    @Override
    public String generateDoc(final PsiElement element, @Nullable final PsiElement originalElement) {
        return DomUtils.getDomElement(element, getTargetDomElement())
                .or(() -> Optional.ofNullable(element)
                        .filter(PomTargetPsiElement.class::isInstance)
                        .map(PomTargetPsiElement.class::cast)
                        .map(PomTargetPsiElement::getTarget)
                        .filter(DomTarget.class::isInstance)
                        .map(DomTarget.class::cast)
                        .map(DomTarget::getDomElement)
                        .filter(getTargetDomElement()::isInstance)
                        .map(getTargetDomElement()::cast))
                .map(DomElement::getXmlTag)
                .map(xmlTag -> {
                    String text = xmlTag.getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\\n", "<br/>");
                    int index = text.lastIndexOf("&lt;");
                    String origin = text.substring(0, index);
                    String start = "";
                    for (int i = origin.length() - 1; i > 0; i--) {
                        if (origin.charAt(i) != ' ') {
                            start = text.substring(0, i + 1);
                            break;
                        }
                    }
                    return "<pre>" + start + text.substring(index) + "</pre>";
                }).orElse(null);
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLookupItem(final PsiManager psiManager, final Object object, final PsiElement element) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLink(final PsiManager psiManager, final String link, final PsiElement context) {
        return null;
    }

    /**
     * 获取文档目标 Dom元素
     */
    abstract Class<T> getTargetDomElement();

}
