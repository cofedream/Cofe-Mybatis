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

package tk.cofe.plugin.mybatis.provider;

import com.intellij.psi.PsiParameter;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 代码完成提供
 *
 * @author : zhengrf
 * @date : 2019-10-07
 */
public interface VariantsProvider<T> {

    default T provider(final String prefixText, final String[] prefixArr, final PsiParameter[] parameters, @NotNull final T res) {
        if (ArrayUtil.isEmpty(parameters)) {
            return res;
        }
        final String prefix = prefixText == null ? "" : prefixText;
        if (ArrayUtil.isEmpty(prefixArr)) {
            if (parameters.length == 1) {
                singleParam(prefix, prefixArr, parameters[0], res);
            } else {
                multiParam(prefix, prefixArr, parameters, res);
            }
        } else {
            emptyPrefix(prefix, prefixArr, parameters, res);
        }
        beforeReturn(prefix, prefixArr, parameters, res);
        return res;
    }

    void singleParam(final String prefixText, final String[] prefixArr, final PsiParameter firstParameter, final T res);

    void multiParam(final String prefixText, final String[] prefixArr, final PsiParameter[] parameters, final T res);

    void emptyPrefix(final String prefixText, final String[] prefixArr, final PsiParameter[] parameters, final T res);

    default void beforeReturn(final String prefixText, final String[] prefixArr, final PsiParameter[] parameters, final T result) {

    }
}
