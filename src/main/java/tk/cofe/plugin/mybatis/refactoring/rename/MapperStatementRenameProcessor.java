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

package tk.cofe.plugin.mybatis.refactoring.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.refactoring.rename.RenameUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.CollectionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;

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
            ClassElement classElement = (ClassElement) domTarget.getDomElement();
            if (classElement != null) {
                classElement.getIdMethod().ifPresent(psiMethod -> {
                    UsageInfo[] methodUsage = RenameUtil.findUsages(psiMethod, newName, true, true, Collections.singletonMap(psiMethod, psiMethod.getName()));
                    if (!CollectionUtils.isEmpty(methodUsage)) {
                        RenamePsiElementProcessor.forElement(psiMethod).renameElement(psiMethod, newName, methodUsage, listener);
                    }
                });
            }
        });
        super.renameElement(element, newName, usages, listener);
    }

}
