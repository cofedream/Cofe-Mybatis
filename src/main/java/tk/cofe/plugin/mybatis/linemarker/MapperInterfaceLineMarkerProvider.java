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

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.bundle.MyBatisBundle;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.icons.MybatisIcons;
import tk.cofe.plugin.mybatis.service.MapperService;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper接口与内部方法标记
 *
 * @author : zhengrf
 * @date : 2019-01-01
 */
public class MapperInterfaceLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    public String getName() {
        return "MapperInterface";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MybatisIcons.MybatisInterface;
    }

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if (PsiJavaUtils.isInterface(element)) {
            markerInterface(((PsiClass) element), result);
        } else if (PsiJavaUtils.isInterfaceMethod(element)) {
            markerMethod(((PsiMethod) element).getContainingClass(), ((PsiMethod) element), result);
        }
    }

    /**
     * 标记接口
     *
     * @param mapperClass 类元素
     * @param method      方法元素
     * @param result      标记结果
     */
    private void markerMethod(PsiClass mapperClass, @NotNull PsiMethod method, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        Collection<Mapper> mappers = MapperService.getInstance(method.getProject()).findMapperXmls(mapperClass);
        if (!mappers.isEmpty() && method.getNameIdentifier() != null) {
            List<XmlTag> xmlMethods = mappers.stream().flatMap(mapperXml -> mapperXml.getClassElements().stream())
                    .filter(classElement -> classElement.getIdMethod().map(psiMethod -> psiMethod.equals(method)).orElse(false))
                    .map(DomElement::getXmlTag).collect(Collectors.toList());
            if (!xmlMethods.isEmpty()) {
                NavigationGutterIconBuilder<PsiElement> methodBuild = NavigationGutterIconBuilder.create(MybatisIcons.NavigateToStatement)
                        .setAlignment(GutterIconRenderer.Alignment.CENTER)
                        .setTargets(xmlMethods)
                        .setTooltipTitle(MyBatisBundle.message("action.navigate.tip", "statement"));
                result.add(methodBuild.createLineMarkerInfo(method.getNameIdentifier()));
            }
        }
    }

    /**
     * 标记接口
     *
     * @param mapperClass 类元素
     * @param result      标记结果
     */
    private void markerInterface(@NotNull PsiClass mapperClass, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        Collection<Mapper> mappers = MapperService.getInstance(mapperClass.getProject()).findMapperXmls(mapperClass);
        PsiIdentifier nameIdentifier = mapperClass.getNameIdentifier();
        if (!mappers.isEmpty() && nameIdentifier != null) {
            result.add(NavigationGutterIconBuilder.create(MybatisIcons.MybatisInterface)
                    .setAlignment(GutterIconRenderer.Alignment.CENTER)
                    .setTargets(mappers.stream().map(mapperXml -> mapperXml.getNamespace().getXmlTag()).collect(Collectors.toList()))
                    .setTooltipTitle(MyBatisBundle.message("action.navigate.tip", "Mapper XML"))
                    .createLineMarkerInfo(nameIdentifier));
        }
    }
}
