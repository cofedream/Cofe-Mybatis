/*
 * Copyright (C) 2019 cofe
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mbsp.lexer;

import com.intellij.lexer.FlexAdapter;
import tk.cofe.plugin.mbsp.parser._MbspLexer;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MbspLexerAdapter extends FlexAdapter {
    public MbspLexerAdapter() {
        super(new _MbspLexer());
    }
}
