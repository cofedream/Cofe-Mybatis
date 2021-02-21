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

package tk.cofe.plugin.mybatis.util;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

/**
 * Element 工具类
 *
 * @author : zhengrf
 * @date : 2019-06-30
 */
public class PsiElementUtils {
    /**
     * 从当前编辑框获取当前光标所在元素
     *
     * @param editor 编辑框
     * @return 当前光标所在元素
     */

    public static PsiElement getElement(Editor editor) {
        return PsiUtilBase.getElementAtCaret(editor);
    }

    /**
     * 从当前编辑框获取当前光标所在元素
     *
     * @param editor 编辑框
     * @return 当前光标所在元素
     */

    public static PsiElement getElement(Editor editor, PsiFile psiFile) {
        return psiFile.findElementAt(editor.getCaretModel().getOffset());
    }

    /**
     * 从当前编辑框获取当前光标所在目标元素
     *
     * @param editor 编辑框
     * @param target 目标元素
     * @return 当前光标所在元素
     */

    public static <T extends PsiElement> T getElement(Editor editor, Class<T> target) {
        PsiElement element = PsiUtilBase.getElementAtCaret(editor);
        if (element == null) {
            return null;
        }
        return PsiTreeUtil.getParentOfType(element, target);
    }

    public static PsiLanguageInjectionHost getInjectionHost(@NotNull CompletionParameters parameters) {
        PsiElement position = parameters.getPosition();
        return InjectedLanguageManager.getInstance(position.getProject()).getInjectionHost(position);
    }
}
