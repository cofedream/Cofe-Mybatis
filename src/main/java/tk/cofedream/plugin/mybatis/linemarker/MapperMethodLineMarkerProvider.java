package tk.cofedream.plugin.mybatis.linemarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.ClassElement;
import tk.cofedream.plugin.mybatis.dom.mapper.model.MapperXml;
import tk.cofedream.plugin.mybatis.icons.MybatisIcons;
import tk.cofedream.plugin.mybatis.service.MapperService;
import tk.cofedream.plugin.mybatis.utils.JavaPsiUtils;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2019-01-01
 */
public class MapperMethodLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    public String getName() {
        return "MapperMethod";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MybatisIcons.NavigateToStatement;
    }

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        //if (JavaPsiUtils.isInterfaceMethod(element)) {
        //    PsiClass mapperClass = (PsiClass) element.getParent();
        //    Collection<MapperXml> mapperXmls = MapperService.getInstance(element.getProject()).findMapperXmls(mapperClass);
        //    if (!mapperXmls.isEmpty()) {
        //        List<ClassElement> collect = mapperXmls.stream().flatMap(mapperXml -> mapperXml.getClassElements().stream()).collect(Collectors.toList());
        //        PsiMethod method = (PsiMethod) element;
        //        List<XmlTag> xmlMethods = collect.stream().filter(classElement -> classElement.getIdValue().map(id -> id.equals(method.getName())).orElse(false))
        //                .map(DomElement::getXmlTag).collect(Collectors.toList());
        //        if (xmlMethods.size() > 0) {
        //            NavigationGutterIconBuilder<PsiElement> methodBuild = NavigationGutterIconBuilder.create(MybatisIcons.NavigateToStatement)
        //                    .setAlignment(GutterIconRenderer.Alignment.CENTER)
        //                    .setTargets(xmlMethods)
        //                    .setTooltipTitle("Navigate to the XML");
        //            if (method.getNameIdentifier() != null) {
        //                result.add(methodBuild.createLineMarkerInfo(method.getNameIdentifier()));
        //            }
        //        }
        //    }
        //}
    }
}
