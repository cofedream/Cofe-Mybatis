package tk.cofe.plugin.mybatis.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.Arrays;

/**
 * 生成 @Param 注解
 * @author : zhengrf
 * @date : 2019-06-18
 */
public class GenerateParamAnnotationIntention implements IntentionAction {
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Generate @Param";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }
        PsiMethod method = PsiJavaUtils.getElement(editor, PsiMethod.class);
        if (method == null || method.getParameterList().isEmpty()) {
            return false;
        }
        return Arrays.stream(method.getParameterList().getParameters()).anyMatch(psiParameter -> !PsiJavaUtils.hasAnnotation(psiParameter, Annotation.PARAM));
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method == null) {
            return;
        }
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            // 有 @Param 则跳过
            if (PsiJavaUtils.hasAnnotation(parameter, Annotation.PARAM)) {
                continue;
            }
            String name = parameter.getName();
            if (StringUtils.isBlank(name)) {
                continue;
            }
            JavaPsiService psiService = JavaPsiService.getInstance(project);
            psiService.importClass(((PsiJavaFile) file), Annotation.PARAM.getQualifiedName());
            psiService.addAnnotation(parameter, Annotation.PARAM.withValue(name).toString());
        }

    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
