package tk.cofedream.plugin.mybatis.declaration;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.ClassElement;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;
import tk.cofedream.plugin.mybatis.service.MapperService;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MybatisGotoDeclarationHandler extends GotoDeclarationHandlerBase {
    @Nullable
    @Override
    public PsiElement getGotoDeclarationTarget(@Nullable PsiElement sourceElement, Editor editor) {
        return Statement.parse(sourceElement).map(statement -> {
            if (sourceElement.getLanguage().is(Language.findInstance(XMLLanguage.class))) {
                if (MapperService.isMapperXmlFile(sourceElement.getContainingFile())) {
                    return statement.process(sourceElement).orElse(null);
                }
            }
            return null;
        }).orElse(null);
    }

    private enum Statement {
        ID("id") {
            @Override
            Optional<? extends PsiElement> process(PsiElement element) {
                Project project = element.getProject();
                Optional<ClassElement> domElement = Optional.ofNullable(DomUtil.findDomElement(element, ClassElement.class));
                JavaPsiService instance = JavaPsiService.getInstance(project);
                return domElement.flatMap(targetElement -> instance.findMethod(targetElement).map(psiMethods -> {
                    for (PsiMethod psiMethod : psiMethods) {
                        if (targetElement.getIdValue().map(id -> id.equals(psiMethod.getName())).orElse(false)) {
                            return psiMethod;
                        }
                    }
                    return null;
                }));
            }
        },
        ;

        private String value;

        Statement(String attributeValue) {
            this.value = attributeValue;
        }

        @NotNull
        public static Optional<Statement> parse(@Nullable PsiElement sourceElement) {
            if (sourceElement == null) {
                return Optional.empty();
            }
            if (!(sourceElement instanceof XmlToken)) {
                return Optional.empty();
            }
            PsiElement xmlAttributeValue = sourceElement.getParent();
            if (!(xmlAttributeValue instanceof XmlAttributeValue)) {
                return Optional.empty();
            }
            PsiElement xmlAttribute = xmlAttributeValue.getParent();
            if (!(xmlAttribute instanceof XmlAttribute)) {
                return Optional.empty();
            }
            for (Statement attribute : values()) {
                if (attribute.getValue().equals(((XmlAttribute) xmlAttribute).getName())) {
                    return Optional.of(attribute);
                }
            }
            return Optional.empty();
        }

        abstract Optional<? extends PsiElement> process(PsiElement element);

        public String getValue() {
            return value;
        }
    }
}
