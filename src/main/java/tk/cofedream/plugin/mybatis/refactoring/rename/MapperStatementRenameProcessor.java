package tk.cofedream.plugin.mybatis.refactoring.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.refactoring.rename.RenameUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.ClassElement;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;
import tk.cofedream.plugin.mybatis.utils.CollectionUtils;
import tk.cofedream.plugin.mybatis.utils.DomUtils;

import java.util.Collections;

/**
 * @author : zhengrf
 * @date : 2019-01-06
 */
public class MapperStatementRenameProcessor extends RenamePsiElementProcessor {

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        if (!(element instanceof PomTargetPsiElement)) {
            return false;
        }
        return DomUtils.resolveToDomTarget(element).map(domTarget -> domTarget.getDomElement() instanceof ClassElement).orElse(false);
    }

    @NotNull
    @Override
    public RenameDialog createRenameDialog(@NotNull Project project, @NotNull PsiElement element, @Nullable PsiElement nameSuggestionContext, @Nullable Editor editor) {
        return super.createRenameDialog(project, element, nameSuggestionContext, editor);
    }

    @Override
    public void renameElement(@NotNull PsiElement element, @NotNull String newName, @NotNull UsageInfo[] usages, @Nullable RefactoringElementListener listener) throws IncorrectOperationException {
        DomUtils.resolveToDomTarget(element).ifPresent(domTarget -> {
            ClassElement domElement = (ClassElement) domTarget.getDomElement();
            JavaPsiService.getInstance(element.getProject()).findMethod(domElement).ifPresent(psiMethods -> {
                for (PsiMethod psiMethod : psiMethods) {
                    if (domElement.getIdValue().map(id -> id.equals(psiMethod.getName())).orElse(false)) {
                        UsageInfo[] methodUsage = RenameUtil.findUsages(psiMethod, newName, true, true, Collections.singletonMap(psiMethod, psiMethod.getName()));
                        if (!CollectionUtils.isEmpty(methodUsage)) {
                            RenamePsiElementProcessor processor = RenamePsiElementProcessor.forElement(psiMethod);
                            processor.renameElement(psiMethod, newName, methodUsage, listener);
                        }
                    }
                }
            });
        });
        super.renameElement(element, newName, usages, listener);
    }

}
