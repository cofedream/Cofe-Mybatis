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

package tk.cofe.plugin.mybatis.psi.reference;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author : zhengrf
 * @date : 2020-01-19
 */
public class CollectionReferenceContributor extends PsiReferenceContributor {

    /**
     * 匹配 foreach 标签
     *
     * @see tk.cofe.plugin.mybatis.dom.model.dynamic.Foreach#TAG
     */
    public static final PsiElementPattern.Capture<XmlTag> FOREACH_PATTERN = psiElement(XmlTag.class)
            .withText(StandardPatterns.string().startsWith("<foreach").endsWith("</foreach>"));
    public static final PsiElementPattern.Capture<XmlAttributeValue> COLLECTION_PATTERN = psiElement(XmlAttributeValue.class)
            .withParent(psiElement(XmlAttribute.class)
                    .withText(StandardPatterns.string().startsWith("collection"))
                    .withParent(FOREACH_PATTERN));

    @Override
    public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(COLLECTION_PATTERN, new CollectionReferenceProvider());
    }
}
