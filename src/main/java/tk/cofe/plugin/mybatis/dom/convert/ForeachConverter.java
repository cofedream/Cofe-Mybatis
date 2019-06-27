package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * foreach标签转换器
 * @author : zhengrf
 * @date : 2019-06-27
 */
public class ForeachConverter {

    public static class Collection extends ResolvingConverter.StringConverter {

        @NotNull
        @Override
        public java.util.Collection<? extends String> getVariants(ConvertContext context) {
            ClassElement classElement = DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class, true);
            if (classElement == null) {
                return Collections.emptyList();
            }
            return classElement.getIdValue().map(id ->
                    JavaPsiService.getInstance(context.getProject()).findPsiMethod(classElement)
                            .map(method -> Arrays.stream(method.getParameters())
                                    .map(jvmParameter -> {
                                        //JvmAnnotation annotation = PsiJavaUtils.getAnnotation(jvmParameter, Annotation.PARAM);
                                        //if (annotation != null) {
                                        //    annotation.findAttribute()
                                        //}
                                        return jvmParameter.getName();
                                    })
                                    .collect(Collectors.toList()))
                            .orElse(Collections.emptyList())
            ).orElse(Collections.emptyList());
            //return classElement.getIdValue().map(id -> JavaPsiService.getInstance(context.getProject()).findPsiClass(classElement)
            //        .map(psiClass -> psiClass.getMethods()).orElse(Collections.<String>emptyList())).orElse(Collections.emptyList());
        }

        @Nullable
        @Override
        public PsiElement resolve(String methodName, ConvertContext context) {
            return null;
            //if (StringUtils.isBlank(methodName)) {
            //    return null;
            //}
            //return JavaPsiService.getInstance(context.getProject())
            //        .findPsiMethod(DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class, true))
            //        .filter(psiMethod -> methodName.equals(psiMethod.getName()))
            //        .orElse(null);
        }

        @Nullable
        @Override
        public LookupElement createLookupElement(String s) {
            return LookupElementBuilder.create(s).withIcon(AllIcons.Nodes.Field);
        }
    }

}
