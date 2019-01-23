package tk.cofedream.plugin.mybatis.dom.mapper.converter.attribute;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;
import tk.cofedream.plugin.mybatis.utils.StringUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * @author : zhengrf
 * @date : 2019-01-23
 */
public class ClassElementConvert {

    public static class Id extends ResolvingConverter.StringConverter {
        @NotNull
        @Override
        public Collection<? extends String> getVariants(ConvertContext context) {
            return Collections.emptyList();
        }

        @Nullable
        @Override
        public PsiElement resolve(String methodName, ConvertContext context) {
            if (StringUtils.isBlank(methodName)) {
                return null;
            }
            ClassElement classElement = DomUtil.getParentOfType(context.getInvocationElement(), ClassElement.class, true);
            return JavaPsiService.getInstance(context.getProject()).findMethod(classElement).map(psiMethods -> {
                for (PsiMethod psiMethod : psiMethods) {
                    if (methodName.equals(psiMethod.getName())) {
                        return psiMethod;
                    }
                }
                return null;
            }).orElse(null);
        }
    }
}
