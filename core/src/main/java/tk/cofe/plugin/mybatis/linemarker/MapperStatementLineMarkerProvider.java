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

package tk.cofe.plugin.mybatis.linemarker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.bundle.MyBatisBundle;
import tk.cofe.plugin.common.icons.MybatisIcons;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.Collection;
import java.util.List;

/**
 * Mapper Xml 行标记
 *
 * @author : zhengrf
 * @date : 2019-01-04
 */
public class MapperStatementLineMarkerProvider implements LineMarkerProvider {

    @Nullable
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (!isTarget(element)) {
            return null;
        }
        return DomUtils.getDomElement(element, ClassElement.class).flatMap(ClassElement::getIdMethod).map(method -> {
            XmlAttribute id = ((XmlTag) element).getAttribute("id");
            if (id == null) {
                return null;
            }
            XmlAttributeValue idVal = id.getValueElement();
            if (idVal == null) {
                return null;
            }
            ASTNode node = idVal.getNode();
            ASTNode idValToken = node.findChildByType(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN);
            if (idValToken == null) {
                return null;
            }
            return NavigationGutterIconBuilder.create(MybatisIcons.NavigateToMethod)
                    .setAlignment(GutterIconRenderer.Alignment.CENTER)
                    .setTargets(method)
                    .setTooltipTitle(MyBatisBundle.message("action.navigate.tip", "method"))
                    .createLineMarkerInfo(idValToken.getPsi());
        }).orElse(null);
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {

    }

    private boolean isTarget(@NotNull PsiElement element) {
        return element instanceof XmlTag && MybatisUtils.isElementWithMapperXMLFile(element) && MybatisUtils.isBaseStatement((XmlElement) element);
    }

}
