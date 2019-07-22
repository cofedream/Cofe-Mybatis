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

package tk.cofe.plugin.mybatis.linemarker;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.icons.MybatisIcons;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Mapper Xml 行标记
 * @author : zhengrf
 * @date : 2019-01-04
 */
public class MapperStatementLineMarkerProvider implements LineMarkerProvider {

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        if (!isTarget(element)) {
            return null;
        }
        DomElement domElement = DomManager.getDomManager(element.getProject()).getDomElement(((XmlTag) element));
        if (!(domElement instanceof ClassElement)) {
            return null;
        }
        Optional<PsiMethod[]> method = JavaPsiService.getInstance(element.getProject()).findPsiMethods(((ClassElement) domElement));
        return method.map(psiMethods -> {
            if (psiMethods.length == 0) {
                return null;
            }
            return new LineMarkerInfo<>(
                    (XmlTag) element,
                    element.getTextRange(),
                    MybatisIcons.NavigateToMethod,
                    Pass.UPDATE_ALL,
                    from -> "Navigate to method",
                    (e, from) -> ((Navigatable) psiMethods[0].getNavigationElement()).navigate(true),
                    GutterIconRenderer.Alignment.CENTER);
        }).orElse(null);
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {

    }

    private boolean isTarget(@NotNull PsiElement element) {
        return element instanceof XmlTag && PsiMybatisUtils.isElementWithMapperXMLFile(element) && PsiMybatisUtils.isBaseStatementElement((XmlElement) element);
    }

}
