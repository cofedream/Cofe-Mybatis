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

package tk.cofe.plugin.mybatis.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * SQL 文件中的SQL 参数完成
 *
 * @author : zhengrf
 * @date : 2019-08-11
 */
public class MbspFileSqlParameterCompletionContributor extends BaseSqlParameterCompletionContributor {

    @Override
    PsiFile getTargetPsiFile(final CompletionParameters parameters, final CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        return InjectedLanguageManager.getInstance(position.getProject()).getTopLevelFile(position);
    }

    @Override
    PsiElement getTargetElement(final CompletionParameters parameters, final CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        return InjectedLanguageManager.getInstance(position.getProject()).getInjectionHost(position);
    }

    String getPrefixText(final PsiElement position, CompletionResultSet result) {
        String resStr = "";
        PsiElement prevSibling = position;
        while ((prevSibling = prevSibling.getPrevSibling()) != null) {
            String text = prevSibling.getText();
            if (text != null) {
                resStr = text.concat(resStr);
            }
        }
        return resStr;
    }

}
