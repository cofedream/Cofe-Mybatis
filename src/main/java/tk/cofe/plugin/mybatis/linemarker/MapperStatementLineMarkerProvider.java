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
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.icons.MybatisIcons;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.service.MapperService;

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
        return element instanceof XmlTag && MapperService.isElementWithMapperXMLFile(element) && MapperService.isBaseStatementElement((XmlElement) element);
    }

}
