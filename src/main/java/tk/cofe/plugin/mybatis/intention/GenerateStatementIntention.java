package tk.cofe.plugin.mybatis.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.CommonProcessors;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Mapper;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
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
        PsiMethod method = PsiJavaUtils.getElement(editor, PsiMethod.class);
        if (method == null) {
            return false;
        }
        PsiClass psiClass = PsiJavaUtils.getElement(editor, PsiClass.class);
        if (psiClass == null) {
            return false;
        }
        if (PsiJavaUtils.hasAnnotations(method, Annotation.STATEMENT_ANNOTATIONS)) {
            return false;
        }
        CommonProcessors.FindFirstProcessor<PsiElement> methodProcessor = new CommonProcessors.FindFirstProcessor<>();
        JavaPsiService.getInstance(project).process(method, methodProcessor);
        PsiElement methodFound = methodProcessor.getFoundValue();
        if (methodFound != null) {
            return false;
        }
        CommonProcessors.FindFirstProcessor<Mapper> classProcessor = new CommonProcessors.FindFirstProcessor<>();
        JavaPsiService.getInstance(project).process(psiClass, classProcessor);
        Mapper classFount = classProcessor.getFoundValue();
        if (classFount == null) {
            return false;
        }
        //if (!()) {
        //    return false;
        //}
        //PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        //PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        //return null != method && null != clazz &&
        //        !JavaUtils.isAnyAnnotationPresent(method, Annotation.STATEMENT_ANNOTATIONS) &&
        //        !isTargetPresentInXml(method) &&
        //        isTargetPresentInXml(clazz);
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {

    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
