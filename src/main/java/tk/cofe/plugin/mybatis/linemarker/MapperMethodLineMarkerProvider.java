package tk.cofe.plugin.mybatis.linemarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.icons.MybatisIcons;

import javax.swing.*;
import java.util.Collection;

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
        //if (PsiJavaUtils.isInterfaceMethod(element)) {
        //    PsiClass mapperClass = (PsiClass) element.getParent();
        //    Collection<Mapper> mapperXmls = MapperService.getInstance(element.getProject()).findMapperXmls(mapperClass);
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
