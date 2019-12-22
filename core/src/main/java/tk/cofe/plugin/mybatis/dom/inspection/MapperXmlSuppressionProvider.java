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

package tk.cofe.plugin.mybatis.dom.inspection;

import com.intellij.codeInspection.XmlSuppressionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Collection;
import tk.cofe.plugin.mybatis.dom.model.tag.Result;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

/**
 * @author : zhengrf
 * @date : 2019-12-22
 */
public class MapperXmlSuppressionProvider extends XmlSuppressionProvider {
    @Override
    public boolean isProviderAvailable(@NotNull final PsiFile file) {
        return MybatisUtils.isMapperXmlFile(file);
    }

    @Override
    public boolean isSuppressedFor(@NotNull final PsiElement element, @NotNull final String inspectionId) {
        if (element instanceof XmlAttributeValue) {
            if (DomUtils.isTargetDomElement(element.getParent(), PropertyAttribute.class)) {
                return DomUtils.isTargetDomElement(element.getParent(), Collection.class);
            }
        }
        if (element instanceof XmlTag) {
            if (DomUtils.isTargetDomElement(element, Result.class)) {
                return DomUtils.isTargetDomElement(element.getParent(), Collection.class);
            }
        }
        return false;
    }

    @Override
    public void suppressForFile(@NotNull final PsiElement element, @NotNull final String inspectionId) {

    }

    @Override
    public void suppressForTag(@NotNull final PsiElement element, @NotNull final String inspectionId) {
    }
}
