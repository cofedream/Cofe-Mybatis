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
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbsp.MbspTypes;
import tk.cofe.plugin.mbsp.psi.MbspExpression;
import tk.cofe.plugin.mbsp.psi.MbspJdbcTypeConfig;
import tk.cofe.plugin.mbsp.psi.MbspModeConfig;
import tk.cofe.plugin.mbsp.psi.MbspResultMapConfig;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Optional;
import java.util.function.Function;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static tk.cofe.plugin.mbsp.MbspKeyword.*;

/**
 * @author : zhengrf
 * @date : 2021-02-20
 */
public class MbspKeywordCompletionContributor extends CompletionContributor {

    public static final PsiElementPattern.Capture<PsiElement> PARAM_EXPRESSION = psiElement()
            .afterLeaf(psiElement().withElementType(MbspTypes.EXPRESSION_START))
            .andOr(psiElement().inside(MbspExpression.class));
    //
    private static final PsiElementPattern.Capture<PsiElement> MODE_EXPRESSION = psiElement().inside(MbspModeConfig.class);
    private static final PsiElementPattern.Capture<PsiElement> JDBC_TYPE_EXPRESSION = psiElement().inside(MbspJdbcTypeConfig.class);
    private static final PsiElementPattern.Capture<PsiElement> RESULT_MAP_EXPRESSION = psiElement().inside(MbspResultMapConfig.class);
    //
    private static final PsiElementPattern.Capture<PsiElement> PARAM_CONFIG_EXPRESSION = psiElement()
            .afterLeafSkipping(psiElement(MbspExpression.class), psiElement().withText(","));

    public MbspKeywordCompletionContributor() {
        installParam();
        // installMode();
        // installJdbcType();
        // installResultMap();
        // installParamConfig();
    }

    private void installParam() {
        extendCompletion(PARAM_EXPRESSION, "demo", "demo.demo");
    }

    private void installMode() {
        extendCompletion(MODE_EXPRESSION, Mode.STRINGS);
    }

    private void installJdbcType() {
        extendCompletion(JDBC_TYPE_EXPRESSION, JdbcType.STRINGS);
    }

    private void installResultMap() {
        extendCompletion(RESULT_MAP_EXPRESSION, parameters -> {
            PsiElement position = parameters.getPosition();
            PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(position.getProject()).getInjectionHost(position);
            Optional<Mapper> domElement = DomUtils.getDomElement(injectionHost, Mapper.class);
            return domElement
                    .map(Mapper::getResultMaps)
                    .map(resultMaps -> resultMaps.stream()
                            .map(resultMap -> resultMap.getIdValue(""))
                            .filter(StringUtils::isNotBlank)
                            .toArray(String[]::new))
                    .orElse(new String[0]);
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
                    final LookupElementBuilder builder = LookupElementBuilder.create(keyword).bold();
                    result.addElement(builder);
                }
            }
        });
    }
}
