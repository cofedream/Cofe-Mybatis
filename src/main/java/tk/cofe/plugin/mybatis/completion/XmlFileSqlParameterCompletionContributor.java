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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * XML 文件中的SQL 参数完成
 *
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class XmlFileSqlParameterCompletionContributor extends BaseSqlParameterCompletionContributor {

    @Override
    PsiFile getTargetPsiFile(@NotNull final CompletionParameters parameters, @NotNull final CompletionResultSet result) {
        return parameters.getOriginalFile();
    }

    @Override
    PsiElement getTargetElement(@NotNull final PsiFile psiFile, @NotNull final CompletionParameters parameters, @NotNull final CompletionResultSet result) {
        return parameters.getPosition();
    }

    @NotNull
    String getPrefixText(@NotNull CompletionResultSet result) {
        String prefix = result.getPrefixMatcher().getPrefix();
        return prefix.substring(0, prefix.lastIndexOf("{") + 1);
    }

    /**
     * 获取前缀
     */
    @NotNull
    String[] getPrefixArray(@NotNull CompletionResultSet result) {
        String prefixText = result.getPrefixMatcher().getPrefix();
        String prefix = prefixText.substring(prefixText.lastIndexOf("{") + 1);
        if (StringUtil.isEmpty(prefix) || !prefix.contains(".")) {
            return new String[0];
        }
        return prefix.substring(0, prefix.lastIndexOf(".")).split("\\.");
    }

}
