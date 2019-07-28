/*
 * Copyright (C) 2019 cofe
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mybatis.documentation;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Sql;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <Sql/> 标签的 documentation
 * @author : zhengrf
 * @date : 2019-07-27
 */
public class SqlTagDocumentation implements DocumentationProvider {

    @Nullable
    @Override
    public String getQuickNavigateInfo(final PsiElement element, final PsiElement originalElement) {
        return null;
    }

    @Nullable
    @Override
    public List<String> getUrlFor(final PsiElement element, final PsiElement originalElement) {
        return Optional.ofNullable(DomUtils.getTargetElement(element, Sql.class))
                .map(sql -> Collections.<String>emptyList()).orElse(null);
    }

    @Nullable
    @Override
    public String generateDoc(final PsiElement element, @Nullable final PsiElement originalElement) {
        return Optional.ofNullable(DomUtils.getTargetElement(element, Sql.class))
                .filter(sql -> sql.getXmlTag() != null).map(DomElement::getXmlTag)
                .map(xmlTag -> "<pre>" + xmlTag.getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\\n", "<br/>") + "</pre>")
                .orElse(null);
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

}
