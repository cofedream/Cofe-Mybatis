/*
 * Copyright (C) 2019-2021 cofe
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

package tk.cofe.plugin.mbel.psi.manipulators;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mbel.MbELFileType;
import tk.cofe.plugin.mbel.psi.MbELReferenceExpression;

/**
 * @author : zhengrf
 * @date : 2021-04-08
 */
public class MbELReferenceExpressionManipulator extends AbstractElementManipulator<MbELReferenceExpression> {
    @Override
    public @Nullable MbELReferenceExpression handleContentChange(@NotNull MbELReferenceExpression element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        String newText = range.replace(element.getText(), newContent);
        PsiFile file = PsiFileFactory.getInstance(element.getProject())
                .createFileFromText("mbel", MbELFileType.INSTANCE,
                        "#{" + newText + "}");
        final PsiElement newExpression = file.getChildren()[1];
        return (MbELReferenceExpression) element.replace(newExpression);
    }
}
