package tk.cofe.plugin.mybatis.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.generate.StatementGenerator;
import tk.cofe.plugin.mybatis.service.MapperService;
import tk.cofe.plugin.mybatis.util.CollectionUtils;
import tk.cofe.plugin.mybatis.util.PsiElementUtils;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;

/**
 * @author : zhengrf
 * @date : 2019-06-18
 */
public class GenerateStatementIntention implements IntentionAction {

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Generate new statement";
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
        PsiMethod method = PsiElementUtils.getElement(editor, PsiMethod.class);
        if (method == null || PsiJavaUtils.hasAnnotations(method, Annotation.STATEMENT_ANNOTATIONS)) {
            return false;
        }
        PsiClass psiClass = method.getContainingClass();
        if (psiClass == null || !MapperService.getInstance(project).isMapperClass(psiClass)) {
            return false;
        }
        return !MapperService.getInstance(project).existStatement(method);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiMethod method = PsiElementUtils.getElement(editor, PsiMethod.class);
        if (null == method) {
            return;
        }
        StatementGenerator.generator(project, editor, file, method);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
