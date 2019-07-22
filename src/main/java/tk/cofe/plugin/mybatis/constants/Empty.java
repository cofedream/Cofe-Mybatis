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

package tk.cofe.plugin.mybatis.constants;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.ResolveResult;

/**
 * @author : zhengrf
 * @date : 2019-06-16
 */
public final class Empty {
    public static final String STRING = "";

    private Empty() {
    }

    public static final class Array {
        public static final Object[] OBJECTS = new Object[0];
        public static final String[] STRING = new String[0];
        public static final ResolveResult[] RESOLVE_RESULT = new ResolveResult[0];
        public static final ProblemDescriptor[] PROBLEM_DESCRIPTOR = new ProblemDescriptor[0];
    }
}
