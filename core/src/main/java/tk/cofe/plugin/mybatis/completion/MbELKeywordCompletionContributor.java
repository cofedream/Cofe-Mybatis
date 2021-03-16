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

package tk.cofe.plugin.mybatis.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.utils.CompletionUtils;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.mbel.psi.MbELJdbcTypeConfig;
import tk.cofe.plugin.mbel.psi.MbELModeConfig;
import tk.cofe.plugin.mbel.psi.MbELReferenceExpression;
import tk.cofe.plugin.mbel.psi.MbELResultMapConfig;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.util.MybatisXMLUtils;

import java.util.Optional;
import java.util.function.Function;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static tk.cofe.plugin.mbel.MbELKeyword.*;

/**
 * @author : zhengrf
 * @date : 2021-02-20
 */
public class MbELKeywordCompletionContributor extends CompletionContributor {

    public static final PsiElementPattern.Capture<PsiElement> REFERENCE_EXPRESSION = psiElement().inside(MbELReferenceExpression.class);
    //
    private static final PsiElementPattern.Capture<PsiElement> MODE_EXPRESSION = psiElement().inside(MbELModeConfig.class);
    private static final PsiElementPattern.Capture<PsiElement> JDBC_TYPE_EXPRESSION = psiElement().inside(MbELJdbcTypeConfig.class);
    private static final PsiElementPattern.Capture<PsiElement> RESULT_MAP_EXPRESSION = psiElement().inside(MbELResultMapConfig.class);
    //
    private static final PsiElementPattern.Capture<PsiElement> PARAM_CONFIG_EXPRESSION = psiElement()
            .afterLeafSkipping(psiElement(MbELReferenceExpression.class), psiElement().withText(","));

    public MbELKeywordCompletionContributor() {
        installParam();
        installMode();
        installJdbcType();
        installResultMap();
        installParamConfig();
    }

    private void installParam() {
        extend(CompletionType.BASIC, REFERENCE_EXPRESSION, new FirstKeywordCompletionProvider(MbELReferenceExpression.class));
    }

    private void installMode() {
        extendCompletion(MODE_EXPRESSION, Mode.STRINGS);
    }

    private void installJdbcType() {
        extendCompletion(JDBC_TYPE_EXPRESSION, JdbcType.STRINGS);
    }

    private void installResultMap() {
        extendCompletion(RESULT_MAP_EXPRESSION, parameters -> {
            PsiLanguageInjectionHost injectionHost = MybatisXMLUtils.getOriginElement(parameters);
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
        extendCompletion(PARAM_CONFIG_EXPRESSION, STRINGS);
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
