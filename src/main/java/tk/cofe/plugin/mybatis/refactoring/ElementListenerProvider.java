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

package tk.cofe.plugin.mybatis.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 重构监听器
 * @author : zhengrf
 * @date : 2019-01-06
 */
public class ElementListenerProvider implements RefactoringElementListenerProvider {
    @Nullable
    @Override
    public RefactoringElementListener getListener(PsiElement element) {
        return new RefactoringElementListener() {
            @Override
            public void elementMoved(@NotNull PsiElement newElement) {

            }

            @Override
            public void elementRenamed(@NotNull PsiElement newElement) {

            }
        };
    }
}
