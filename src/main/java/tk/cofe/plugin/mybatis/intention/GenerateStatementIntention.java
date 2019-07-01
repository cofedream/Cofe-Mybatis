package tk.cofe.plugin.mybatis.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.service.MapperService;
import tk.cofe.plugin.mybatis.util.CollectionUtils;
import tk.cofe.plugin.mybatis.util.PsiElementUtils;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;

import javax.swing.*;

/**
 * @author : zhengrf
 * @date : 2019-06-18
 */
public class GenerateStatementIntention implements IntentionAction {

    private static final String STATEMENT_TYPE = "Statement Type";

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
        if (method == null) {
            return false;
        }
        if (PsiJavaUtils.hasAnnotations(method, Annotation.STATEMENT_ANNOTATIONS)) {
            return false;
        }
        return CollectionUtils.isEmpty(MapperService.getInstance(project).findStatement(method));
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiMethod method = PsiElementUtils.getElement(editor, PsiMethod.class);
        if (null == method) {
            return;
        }
        JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<StatementTypeEnum>(STATEMENT_TYPE, StatementTypeEnum.values()) {
            @Override
            public Icon getIconFor(final StatementTypeEnum value) {
                return AllIcons.Nodes.Tag;
            }

            @NotNull
            @Override
            public String getTextFor(StatementTypeEnum value) {
                return value.getDesc();
            }

            @Override
            public PopupStep onChosen(StatementTypeEnum selectedValue, boolean finalChoice) {
                return doFinalStep(() -> WriteCommandAction.writeCommandAction(project, file).run(() -> {
                    PsiClass psiClass = method.getContainingClass();
                    if (null == psiClass) {
                        return;
                    }
                    MapperService.getInstance(project).findMapperXmls(psiClass).forEach(mapper -> selectedValue.processCreateStatement(mapper, method, project));
                }));
            }
        }).showInBestPositionFor(editor);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
