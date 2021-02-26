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

package tk.cofe.plugin.mbl.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbl.psi.MblTokenGroups;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhengrf
 * @date : 2021-02-25
 */
public class MblSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> KEY_MAP;

    public static final TextAttributesKey KEYWORDS =
            TextAttributesKey.createTextAttributesKey("MBL.KEYWORDS", DefaultLanguageHighlighterColors.KEYWORD);

    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    static {
        KEY_MAP = new HashMap<>();
        fillMap(KEY_MAP, MblTokenGroups.PARAM_CONFIG_KEYWORDS, KEYWORDS);
    }

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new MblHighlightingLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return pack(EMPTY_KEYS, KEY_MAP.get(tokenType));
    }

}
