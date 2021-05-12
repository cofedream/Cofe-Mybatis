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

import com.intellij.psi.PsiField;

/**
 * @author : zhengrf
 * @date : 2021-03-09
 */
public class PsiFieldUtils {
    /**
     * 判断是否为序列化字段
     *
     * @param psiField 字段
     */
    public static boolean notSerialField(PsiField psiField) {
        return !"serialVersionUID".equals(psiField.getName());
    }

    /**
     * 判断是否有其中的任意一个修饰符
     */
    public static boolean anyMatch(PsiField psiField, String... psiModifiers) {
        for (String psiModifier : psiModifiers) {
            if (psiField.hasModifierProperty(psiModifier)) {
                return true;
            }
        }
        return false;
    }
}
