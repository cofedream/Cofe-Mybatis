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
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.icons.MybatisIcons;
import tk.cofe.plugin.mybatis.service.MapperService;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper接口与内部方法标记
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
            markerInterface(element, result);
        } else if (PsiJavaUtils.isInterfaceMethod(element)) {
            PsiClass mapperClass = (PsiClass) element.getParent();
            markerMethod(mapperClass, element, result);
        }
    }

    /**
     * 标记接口
     * @param mapperClass 类元素
     * @param element     方法元素
     * @param result      标记结果
     */
    private void markerMethod(PsiClass mapperClass, @NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        Collection<Mapper> mappers = MapperService.getInstance(element.getProject()).findMapperXmls(mapperClass);
        if (!mappers.isEmpty()) {
            List<ClassElement> collect = mappers.stream().flatMap(mapperXml -> mapperXml.getClassElements().stream()).collect(Collectors.toList());
            PsiMethod method = (PsiMethod) element;
            List<XmlTag> xmlMethods = collect.stream().filter(classElement -> classElement.getIdValue().map(id -> id.equals(method.getName())).orElse(false))
                    .map(DomElement::getXmlTag).collect(Collectors.toList());
            if (!xmlMethods.isEmpty() && method.getNameIdentifier() != null) {
                NavigationGutterIconBuilder<PsiElement> methodBuild = NavigationGutterIconBuilder.create(MybatisIcons.NavigateToStatement)
                        .setAlignment(GutterIconRenderer.Alignment.CENTER)
                        .setTargets(xmlMethods)
                        .setTooltipTitle("Navigate to the Statement");
                result.add(methodBuild.createLineMarkerInfo(method.getNameIdentifier()));
            }
        }
    }

    /**
     * 标记接口
     * @param element 类元素
     * @param result  标记结果
     */
    private void markerInterface(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        PsiClass mapperClass = (PsiClass) element;
        Collection<Mapper> mappers = MapperService.getInstance(element.getProject()).findMapperXmls(mapperClass);
        if (!mappers.isEmpty()) {
            NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(MybatisIcons.MybatisInterface)
                    .setAlignment(GutterIconRenderer.Alignment.CENTER)
                    .setTargets(mappers.stream().map(mapperXml -> mapperXml.getNamespace().getXmlTag()).collect(Collectors.toList()))
                    .setTooltipTitle("Navigate to the Mapper XML");
            PsiIdentifier nameIdentifier = mapperClass.getNameIdentifier();
            if (nameIdentifier != null) {
                result.add(builder.createLineMarkerInfo(nameIdentifier));
            }
        }
    }
}
