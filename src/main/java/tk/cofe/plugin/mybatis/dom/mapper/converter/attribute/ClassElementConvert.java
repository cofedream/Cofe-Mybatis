package tk.cofe.plugin.mybatis.dom.mapper.converter.attribute;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author : zhengrf
 * @date : 2019-01-23
 */
public class ClassElementConvert {

    public static class Id extends ResolvingConverter.StringConverter {
        @NotNull
        @Override
        public Collection<? extends String> getVariants(ConvertContext context) {
            DomElement currentElement = context.getInvocationElement();
            ClassElement classElement = DomUtils.getParentOfType(currentElement, ClassElement.class, true);
            if (classElement == null) {
                return Collections.emptyList();
            }
            return classElement.getIdValue().map(id -> JavaPsiService.getInstance(context.getProject()).findClass(classElement).map(psiClass -> {
                Collection<String> res = new HashSet<>();
                for (PsiMethod method : psiClass.getMethods()) {
                    res.add(method.getName());
                }
                return res;
            }).orElse(Collections.emptyList())).orElse(Collections.emptyList());
        }

        @Nullable
        @Override
        public PsiElement resolve(String methodName, ConvertContext context) {
            if (StringUtils.isBlank(methodName)) {
                return null;
            }
            ClassElement classElement = DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class, true);
            return JavaPsiService.getInstance(context.getProject()).findMethod(classElement).map(psiMethod -> {
                if (methodName.equals(psiMethod.getName())) {
                    return psiMethod;
                }
                return null;
            }).orElse(null);
        }
    }
}
