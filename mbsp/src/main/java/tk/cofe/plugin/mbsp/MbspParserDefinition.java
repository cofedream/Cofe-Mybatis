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

package tk.cofe.plugin.mbsp;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbsp.lexer.MbspLexerAdapter;
import tk.cofe.plugin.mbsp.parser.MbspParser;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MbspParserDefinition implements ParserDefinition {
    private static final IFileElementType MBSP_FILE = new IFileElementType(MbspLanguage.INSTANCE);

    private static final TokenSet WHITE_SPACE_TOKENS = TokenSet.create(TokenType.WHITE_SPACE);

    @NotNull
    @Override
    public Lexer createLexer(final Project project) {
        return new MbspLexerAdapter();
    }

    @Override
    public PsiParser createParser(final Project project) {
        return new MbspParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return MBSP_FILE;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiElement createElement(final ASTNode node) {
        return MbspTypes.Factory.createElement(node);
    }

    @Override
    public PsiFile createFile(final FileViewProvider viewProvider) {
        return new MbspFile(viewProvider);
    }
}
