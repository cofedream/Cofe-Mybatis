package tk.cofe.plugin.mybatis.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.enums.AttributeEnums;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.EnumUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MybatisGotoDeclarationHandler extends GotoDeclarationHandlerBase {
    @Nullable
    @Override
    public PsiElement getGotoDeclarationTarget(@Nullable PsiElement sourceElement, Editor editor) {
        if (!PsiMybatisUtils.isElementWithMapperXMLFile(sourceElement)) {
            return null;
        }
        XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(sourceElement, XmlAttribute.class);
        return xmlAttribute == null ? null : EnumUtils.parse(StatementAttribute.values(), xmlAttribute).map(statement -> {
            if (sourceElement.getLanguage().is(XMLLanguage.INSTANCE)) {
                if (PsiMybatisUtils.isMapperXmlFile(sourceElement.getContainingFile())) {
                    return statement.process(sourceElement).orElse(null);
                }
            }
            return null;
        }).orElse(null);
    }

    private enum StatementAttribute implements AttributeEnums {
        ID("id") {
            @Override
            Optional<? extends PsiElement> process(PsiElement element) {
                Project project = element.getProject();
                Optional<ClassElement> domElement = Optional.ofNullable(DomUtils.findDomElement(element, ClassElement.class));
                JavaPsiService instance = JavaPsiService.getInstance(project);
                return domElement.flatMap(targetElement -> instance.findPsiMethods(targetElement).map(psiMethods -> {
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

        StatementAttribute(String attributeValue) {
            this.value = attributeValue;
        }

        @NotNull
        public static Optional<StatementAttribute> parse(@NotNull XmlAttribute xmlAttribute) {
            for (StatementAttribute attribute : values()) {
                if (attribute.getValue().equals(xmlAttribute.getName())) {
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
