/*
 * Copyright (C) 2019 cofe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
