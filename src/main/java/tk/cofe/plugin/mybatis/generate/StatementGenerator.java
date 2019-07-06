package tk.cofe.plugin.mybatis.generate;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.service.MapperService;

import javax.swing.*;

/**
 * Statement 生成
 *
 * @author : zhengrf
 * @date : 2019-07-06
 */
public class StatementGenerator {
    private static final String STATEMENT_TYPE = "Statement Type";

    /**
     * 生成Statement
     *
     * @param project 项目
     * @param editor  编辑窗口
     * @param file    当前文件
     * @param method  接口方法
     */
    public static void generator(@NotNull final Project project, final Editor editor, final PsiFile file, final PsiMethod method) {
        JBPopupFactory.getInstance().createListPopup(createGeneratorPopup(project, file, method)).showInBestPositionFor(editor);
    }

    /**
     * 创建生成提示窗口
     *
     * @param project 项目
     * @param file    当前文件
     * @param method  接口方法
     * @return 窗口
     */
    private static BaseListPopupStep<StatementTypeEnum> createGeneratorPopup(@NotNull final Project project, final PsiFile file, final PsiMethod method) {
        return new BaseListPopupStep<StatementTypeEnum>(STATEMENT_TYPE, StatementTypeEnum.values()) {
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
        };
    }
}
