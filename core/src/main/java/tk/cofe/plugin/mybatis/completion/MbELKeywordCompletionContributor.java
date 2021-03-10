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
import com.intellij.icons.AllIcons;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.CompletionUtils;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.common.utils.PsiJavaUtils;
import tk.cofe.plugin.common.utils.PsiTypeUtils;
import tk.cofe.plugin.mbel.psi.MbELJdbcTypeConfig;
import tk.cofe.plugin.mbel.psi.MbELModeConfig;
import tk.cofe.plugin.mbel.psi.MbELReferenceExpression;
import tk.cofe.plugin.mbel.psi.MbELResultMapConfig;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Bind;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Foreach;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.MybatisXMLUtils;

import java.util.Collections;
import java.util.Map;
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
        extend(CompletionType.BASIC, REFERENCE_EXPRESSION, new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                final PsiElement position = completionParameters.getPosition();
                // 获取前缀
                if (Optional.of(position)
                        .map(info -> PsiTreeUtil.getParentOfType(info, MbELReferenceExpression.class))
                        .map(expr -> expr.textContains('.'))
                        .orElse(false)) {
                    return;
                }
                PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(position.getProject()).getInjectionHost(position);
                for (Bind bind : MybatisXMLUtils.getTheBindTagInParents(injectionHost)) {
                    DomUtils.getAttributeValueOpt(bind.getName()).ifPresent(bindName -> {
                        result.addElement(LookupElementBuilder.create(bindName)
                                .withTailText(DomUtils.getAttributeValueOpt(bind.getValue()).map(v -> "value=\"" + v + "\"").orElse(null), true)
                                .withTypeText("<bind/>")
                                .withIcon(PlatformIcons.XML_TAG_ICON)
                                .bold());
                    });
                }
                for (Foreach foreach : MybatisXMLUtils.getTheForeachTagInParents(injectionHost)) {
                    DomUtils.getAttributeValueOpt(foreach.getItem()).ifPresent(itemName -> {
                        result.addElement(LookupElementBuilder.create(itemName)
                                .withTailText(DomUtils.getAttributeValueOpt(foreach.getCollection()).map(v -> "collection=\"" + v + "\"").orElse(null), true)
                                .withTypeText("<foreach/>")
                                .withIcon(PlatformIcons.XML_TAG_ICON)
                                .bold());
                    });
                    DomUtils.getAttributeValueOpt(foreach.getIndex()).ifPresent(itemName -> {
                        result.addElement(LookupElementBuilder.create(itemName)
                                .withTailText(DomUtils.getAttributeValueOpt(foreach.getCollection()).map(v -> "collection=\"" + v + "\"").orElse(null), true)
                                .withTypeText("<foreach/>")
                                .withIcon(PlatformIcons.XML_TAG_ICON)
                                .bold());
                    });
                }
                DomUtils.getDomElement(injectionHost, ClassElement.class).flatMap(ClassElement::getIdMethod).ifPresent(psiMethod -> {
                    // 方法参数
                    if (!psiMethod.hasParameters()) {
                        return;
                    }
                    final PsiParameterList parameterList = psiMethod.getParameterList();
                    if (parameterList.getParametersCount() == 1) {
                        Optional.ofNullable(parameterList.getParameter(0)).ifPresent(parameter -> {
                            final Annotation.Value value = Annotation.PARAM.getValue(parameter);
                            if (value != null) {
                                result.addElement(LookupElementBuilder.create(value.getAnnotation(), value.getValue())
                                        .withIcon(AllIcons.Nodes.Annotationtype)
                                        .withTypeText(Optional.of(parameter.getType())
                                                .map(PsiType::getPresentableText)
                                                .orElse(""))
                                        .bold());
                                result.addElement(LookupElementBuilder.create("param1").withIcon(AllIcons.Nodes.Parameter).bold());
                            } else {
                                for (Map.Entry<String, PsiMember> entry : Optional.of(parameter.getType())
                                        .filter(PsiTypeUtils::notPrimitiveType) // 非基础类型
                                        .map(psiType -> CompletionUtils.getTheGetMethodAndField(((PsiClassType) psiType)))
                                        .orElse(Collections.emptyMap()).entrySet()) {
                                    final String k = entry.getKey();
                                    final PsiMember v = entry.getValue();
                                    result.addElement(LookupElementBuilder.create(v, k)
                                            .withIcon(PsiJavaUtils.getPsiMemberIcon(v))
                                            .withTypeText(Optional.ofNullable(PsiJavaUtils.getPsiMemberType(v))
                                                    .map(PsiType::getPresentableText)
                                                    .orElse(""))
                                            .bold());
                                }
                            }
                        });
                    } else if (parameterList.getParametersCount() > 1) {
                        final PsiParameter[] parameters = parameterList.getParameters();
                        for (int i = 0; i < parameters.length; i++) {
                            result.addElement(LookupElementBuilder.create("param" + (i + 1)).withIcon(AllIcons.Nodes.Parameter).bold());
                            final Annotation.Value value = Annotation.PARAM.getValue(parameters[i]);
                            if (value != null) {
                                result.addElement(LookupElementBuilder.create(value.getAnnotation(), value.getValue())
                                        .withIcon(AllIcons.Nodes.Annotationtype)
                                        .withTypeText(Optional.of(parameters[i].getType())
                                                .map(PsiType::getPresentableText)
                                                .orElse(""))
                                        .bold());
                            }
                        }
                    }
                });
            }
        });
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
