/*
 * Copyright (C) 2019-2023 cofe
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

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.utils.CompletionUtils;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.mybatis.constant.ElementPattern;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.Optional;
import java.util.function.Function;

import static tk.cofe.plugin.mbel.MbELKeyword.*;
import static tk.cofe.plugin.mybatis.constant.ElementPattern.MbEL;

/**
 * @author : zhengrf
 * @date : 2021-02-20
 */
public class MbELKeywordCompletionContributor extends CompletionContributor {

    public MbELKeywordCompletionContributor() {
        installMode();
        installJdbcType();
        installResultMap();
        installParamConfig();
    }

    private void installMode() {
        extendCompletion(MbEL.MODE_EXPRESSION, Mode.STRINGS);
    }

    private void installJdbcType() {
        extendCompletion(MbEL.JDBC_TYPE_EXPRESSION, JdbcType.STRINGS);
    }

    private void installResultMap() {
        extendCompletion(ElementPattern.MbEL.RESULT_MAP_EXPRESSION, parameters -> {
            PsiLanguageInjectionHost injectionHost = MybatisUtils.getOriginElement(parameters);
            Optional<Mapper> domElement = DomUtils.getDomElement(injectionHost, Mapper.class);
            return domElement
                    .map(Mapper::getResultMaps)
                    .map(resultMaps -> resultMaps.stream()
                            .map(resultMap -> resultMap.getIdValue(""))
                            .filter(StringUtils::isNotBlank)
                            .toArray(String[]::new))
                    .orElse(CompletionUtils.EMPTY_STRING_ARRAY);
        });
    }

    private void installParamConfig() {
        extendCompletion(MbEL.PARAM_CONFIG_EXPRESSION, STRINGS);
    }

    private void extendCompletion(final PsiElementPattern.Capture<PsiElement> pattern,
                                  final String... keywords) {
        extendCompletion(pattern, parameters -> keywords);
    }

    private void extendCompletion(final PsiElementPattern.Capture<PsiElement> pattern,
                                  final Function<CompletionParameters, String[]> getKeywords) {
        extend(CompletionType.BASIC, pattern, new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                String[] keywords = getKeywords.apply(parameters);
                for (String keyword : keywords) {
                    result.addElement(LookupElementBuilder.create(keyword).bold());
                }
            }
        });
    }
}
