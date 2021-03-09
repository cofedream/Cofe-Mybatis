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

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.utils.*;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.mbel.psi.MbELModeConfig;
import tk.cofe.plugin.mbel.psi.MbELResultMapConfig;
import tk.cofe.plugin.mognl.psi.MOgnlExpression;
import tk.cofe.plugin.mognl.psi.MOgnlReferenceExpression;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mbel.psi.MbELJdbcTypeConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static tk.cofe.plugin.mbel.MbELKeyword.JdbcType;
import static tk.cofe.plugin.mbel.MbELKeyword.Mode;
import static tk.cofe.plugin.mbel.MbELKeyword.STRINGS;

/**
 * @author : zhengrf
 * @date : 2021-02-20
 */
public class MOgnlKeywordCompletionContributor extends CompletionContributor {

    public static final PsiElementPattern.Capture<PsiElement> REFERENCE_EXPRESSION = psiElement().inside(MOgnlReferenceExpression.class);
    //
    private static final PsiElementPattern.Capture<PsiElement> MODE_EXPRESSION = psiElement().inside(MbELModeConfig.class);
    private static final PsiElementPattern.Capture<PsiElement> JDBC_TYPE_EXPRESSION = psiElement().inside(MbELJdbcTypeConfig.class);
    private static final PsiElementPattern.Capture<PsiElement> RESULT_MAP_EXPRESSION = psiElement().inside(MbELResultMapConfig.class);
    //
    private static final PsiElementPattern.Capture<PsiElement> PARAM_CONFIG_EXPRESSION = psiElement()
            .afterLeafSkipping(psiElement(MOgnlExpression.class), psiElement().withText(","));

    public MOgnlKeywordCompletionContributor() {
        installParam();
        // installMode();
        // installJdbcType();
        // installResultMap();
        // installParamConfig();
    }

    private void installParam() {
        extendCompletion(REFERENCE_EXPRESSION, completionParameters -> {
            final PsiElement position = completionParameters.getPosition();
            // 获取前缀
            final String refExp = Optional.of(position)
                    .map(info -> PsiTreeUtil.getParentOfType(info, MOgnlReferenceExpression.class))
                    .map(PsiElement::getText)
                    .orElse("");
            if (refExp.contains(".")) {
                return CompletionUtils.EMPTY_STRING_ARRAY;
            }
            PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(position.getProject()).getInjectionHost(position);
            return DomUtils.getDomElement(injectionHost, ClassElement.class).flatMap(ClassElement::getIdMethod)
                    .map(psiMethod -> {
                        // 方法参数
                        if (!psiMethod.hasParameters()) {
                            return CompletionUtils.EMPTY_STRING_ARRAY;
                        }
                        final PsiParameterList parameterList = psiMethod.getParameterList();
                        if (parameterList.getParametersCount() == 1) {
                            return Optional.ofNullable(parameterList.getParameter(0)).map(parameter ->
                                    Optional.ofNullable(Annotation.PARAM.getValue(parameter))
                                            .map(Annotation.Value::getValue)
                                            .map(val -> new String[] {val, "param1"})
                                            .orElseGet(() ->
                                                    Optional.of(parameter.getType())
                                                            .filter(PsiTypeUtils::isCustomType)
                                                            .map(PsiClassType.class::cast)
                                                            .map(psiClassType -> {
                                                                List<String> list = new ArrayList<>();
                                                                CompletionUtils.getPsiClassTypeVariants((psiClassType),
                                                                        field -> list.add(field.getName()),
                                                                        method -> list.add(PsiMethodUtils.replaceGetPrefix(method)));
                                                                return list.toArray(String[]::new);
                                                            })
                                                            .orElse(new String[] {"param1"})))
                                    .orElse(CompletionUtils.EMPTY_STRING_ARRAY);
                        }
                        final PsiParameter[] parameters = parameterList.getParameters();
                        String[] vars = new String[parameters.length];
                        for (int i = 0; i < parameters.length; i++) {
                            vars[i] = "param" + (i + 1);
                        }
                        return vars;
                    }).orElse(CompletionUtils.EMPTY_STRING_ARRAY);
        });
    }
    // private void installParam() {
    //     extendCompletion(REFERENCE_EXPRESSION, completionParameters -> {
    //         final PsiElement position = completionParameters.getPosition();
    //         // 获取前缀
    //         // final String prefixStr = Optional.of(position)
    //         //         .map(info -> PsiTreeUtil.getParentOfType(info, MblReferenceExpression.class))
    //         //         .map(PsiElement::getText)
    //         //         .map(CompletionUtils::getPrefixStr)
    //         //         .orElse("");
    //         // System.out.println("text:" + prefixStr);
    //         // PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(position.getProject()).getInjectionHost(position);
    //         // final String[] strings = DomUtils.getDomElement(injectionHost, ClassElement.class).flatMap(ClassElement::getIdMethod)
    //         //         .map(psiMethod -> {
    //         //             // 方法参数
    //         //             if (!psiMethod.hasParameters()) {
    //         //                 return CompletionUtils.EMPTY_STRING_ARRAY;
    //         //             }
    //         //             final PsiParameterList parameterList = psiMethod.getParameterList();
    //         //             if (parameterList.getParametersCount() == 1) {
    //         //                 return new String[] {"param"};
    //         //             }
    //         //             final PsiParameter[] parameters = parameterList.getParameters();
    //         //             String[] vars = new String[parameters.length];
    //         //             for (int i = 0; i < parameters.length; i++) {
    //         //                 vars[i] = "param" + (i + 1);
    //         //             }
    //         //             return vars;
    //         //         }).orElse(new String[0]);
    //         System.out.println("strings");
    //         return CompletionUtils.EMPTY_STRING_ARRAY;
    //     });
    // }

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
                final String prefix = result.getPrefixMatcher().getPrefix();
                String[] keywords = getKeywords.apply(parameters);
                for (String keyword : keywords) {
                    final LookupElementBuilder builder = LookupElementBuilder.create(keyword).bold();
                    result.addElement(builder);
                }
            }
        });
    }
}
