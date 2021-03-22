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

package tk.cofe.plugin.mbel.psi;

import com.intellij.psi.tree.TokenSet;
import tk.cofe.plugin.mbel.MbELTypes;

/**
 * @author : zhengrf
 * @date : 2021-02-06
 */
public class MOgnlTokenGroups implements MbELTypes {

    // parameter config
    public static final TokenSet PARAM_CONFIG_KEYWORDS = TokenSet.create(MODE_KEYWORD, JAVA_TYPE_KEYWORD,
            JDBC_TYPE_KEYWORD, JDBC_TYPE_KEYWORD_NAME, NUMERIC_SCALE_KEYWORD, TYPE_HANDLER_KEYWORD, RESULT_MAP_KEYWORD);

}
