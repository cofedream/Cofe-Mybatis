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

package tk.cofe.plugin.common.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
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

    /**
     * 获取元素类型
     */
    @NotNull
    public static IElementType getElementType(PsiElement element) {
        return element.getNode().getElementType();
    }

    /**
     * 获取目标子元素
     *
     * @param element      父元素
     * @param iElementType 元素类型
     * @return 子元素
     */
    public static PsiElement getSubElement(PsiElement element, IElementType iElementType) {
        if (element == null || iElementType == null) {
            return null;
        }
        PsiElement child = element.getFirstChild();
        while (child != null) {
            if (getElementType(child) == iElementType) {
                return child;
            }
            child = child.getNextSibling();
        }
        return null;
    }
}
