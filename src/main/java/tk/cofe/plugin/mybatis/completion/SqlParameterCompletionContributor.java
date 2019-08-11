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

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.constants.Empty;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.CompletionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;
import tk.cofe.plugin.mybatis.util.PsiTypeUtils;
import tk.cofe.plugin.mybatis.util.StringUtils;

import javax.swing.*;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * XML 文件中的SQL 参数完成
 *
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class SqlParameterCompletionContributor extends CompletionContributor {

    /**
     * 权重
     */
    private static final double PRIORITY = 100;
    private static final RowIcon PRIVATE_FIELD_ICON = new RowIcon(PlatformIcons.FIELD_ICON, PlatformIcons.PRIVATE_ICON);

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        if (!PsiMybatisUtils.isMapperXmlFile(parameters.getOriginalFile())) {
            return;
        }
        if (isSupport(parameters)) {
            ClassElement classElement = DomUtils.getTargetElement(parameters.getPosition(), ClassElement.class);
            if (classElement != null) {
                classElement.getIdMethod().ifPresent(psiMethod -> {
                    String originPrefix = getOriginPrefix(result);
                    addPsiParamaterVariants(originPrefix, getPrefix(originPrefix), psiMethod.getParameterList().getParameters(), result);
                });
            }
        }
    }

    private void addPsiParamaterVariants(@NotNull final String originPrefix, @NotNull String[] prefixs, final PsiParameter[] parameters, @NotNull CompletionResultSet result) {
        if (parameters.length == 0) {
            return;
        }
        // 根据 paramters 和 prefix 获取元素
        if (prefixs.length == 0) {
            if (parameters.length == 1) {
                Annotation.Value value = Annotation.PARAM.getValue(parameters[0]);
                if (value == null) {
                    // 如果是自定义类型,则读取类字段,如果不是则不做处理使用后续的 param1
                    if (PsiTypeUtils.isCustomType(parameters[0].getType()) && parameters[0].getType() instanceof PsiClassType) {
                        addPsiClassTypeVariants(originPrefix, prefixs, (PsiClassType) parameters[0].getType(), result);
                    }
                } else {
                    result.addElement(createLookupElement(originPrefix, value.getValue(), parameters[0].getType().getPresentableText(), AllIcons.Nodes.Parameter));
                }
            } else {
                for (PsiParameter psiParameter : parameters) {
                    Optional.ofNullable(Annotation.PARAM.getValue(psiParameter)).ifPresent(value -> result.addElement(createLookupElement(originPrefix, value.getValue(), psiParameter.getType().getPresentableText(), AllIcons.Nodes.Parameter)));
                }
            }
        } else {
            PsiType type = CompletionUtils.getPrefixType(prefixs[0], parameters);
            // 自定义类类型则取字段和方法
            if (type != null && PsiTypeUtils.isCustomType(type)) {
                addPsiClassTypeVariants(originPrefix, prefixs, CompletionUtils.getTargetPsiClass(prefixs, (PsiClassType) type), result);
            }
        }
        addParamsVariants(originPrefix, result, prefixs, parameters);
        result.stopHere();
    }

    @NotNull
    private String getOriginPrefix(@NotNull CompletionResultSet result) {
        return result.getPrefixMatcher().getPrefix();
    }

    /**
     * 获取前缀
     */
    @NotNull
    private String[] getPrefix(@NotNull String originPrefix) {
        String prefix = originPrefix.substring(originPrefix.lastIndexOf("{") + 1);
        if (StringUtils.isBlank(prefix) || !prefix.contains(".")) {
            return Empty.Array.STRING;
        }
        String substring = prefix.substring(0, prefix.lastIndexOf("."));
        return substring.split("\\.");
    }

    /**
     * 判断是否支持代码完成
     */
    private boolean isSupport(@NotNull CompletionParameters parameters) {
        String text = parameters.getOriginalFile().getText();
        for (int i = parameters.getOffset() - 1; i > 0; i--) {
            char c = text.charAt(i);
            if (c == '}') {
                return false;
            }
            char beforeChart = text.charAt(i - 1);
            if (c == '{' && (beforeChart == '#' || beforeChart == '$')) {
                for (int j = parameters.getOffset(); j < text.length(); j++) {
                    if (text.charAt(j) == '}') {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    /**
     * 通过类添加提示
     *
     * @param originPrefix 原前缀
     * @param prefixs      前缀
     * @param psiType      Java类类型
     * @param result       结果集
     */
    private void addPsiClassTypeVariants(final String originPrefix, String[] prefixs, @Nullable PsiClassType psiType, @NotNull CompletionResultSet result) {
        if (psiType == null) {
            return;
        }
        PsiClass psiClass = psiType.resolve();
        if (psiClass == null) {
            return;
        }
        String prefiex = String.join(".", prefixs);
        if (prefiex.length() > 0) {
            prefiex = prefiex + ".";
        }
        if (psiClass.isEnum()) {
            addMethodsVariants(originPrefix, prefiex, psiClass.getMethods(), result);
        } else {
            addFieldsVariants(originPrefix, prefiex, psiClass.getAllFields(), result);
            addMethodsVariants(originPrefix, prefiex, psiClass.getAllMethods(), result);
        }
    }

    private void addFieldsVariants(final String originPrefix, final String prefiex, final PsiField[] fields, @NotNull final CompletionResultSet result) {
        for (PsiField field : fields) {
            if (CompletionUtils.isTargetField(field)) {
                createLookupElement(originPrefix, prefiex, field.getName(), field.getType().getPresentableText(), PsiTypeUtils.isCustomType(field.getType()) ? PlatformIcons.CLASS_ICON : PRIVATE_FIELD_ICON, result::addElement);
            }
        }
    }

    private void addMethodsVariants(final String originPrefix, final String prefiex, final PsiMethod[] methods, @NotNull final CompletionResultSet result) {
        for (PsiMethod method : methods) {
            if (CompletionUtils.isTargetMethod(method) && method.getReturnType() != null) {
                createLookupElement(originPrefix, prefiex, PsiJavaUtils.processGetMethodName(method), method.getReturnType().getPresentableText(), PlatformIcons.METHOD_ICON, result::addElement);
            }
        }
    }

    /**
     * 添加 param1-paramn 的提示
     *
     * @param originPrefix     原前缀
     * @param result           结果集
     * @param prefixs          前缀
     * @param methodParameters 方法参数
     */
    private void addParamsVariants(final String originPrefix, @NotNull CompletionResultSet result, @NotNull String[] prefixs, PsiParameter[] methodParameters) {
        // 方法参数大于一个的时候才进行 param1->paramN的提示,否则仅提示类内部字段
        if (prefixs.length != 0 && methodParameters.length <= 1) {
            return;
        }
        for (int i = 0; i < methodParameters.length; i++) {
            result.addElement(createLookupElement(originPrefix, "param" + (i + 1), methodParameters[i].getType().getPresentableText(), AllIcons.Nodes.Parameter, methodParameters.length - i));
        }
    }

    /**
     * 创建提示
     */
    private void createLookupElement(@NotNull String originPrefix, @Nullable final String prefix, @Nullable final String name, final String typeText, final Icon icon, final Consumer<LookupElement> consumer) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(typeText)) {
            return;
        }
        consumer.accept(createLookupElement(originPrefix, prefix + name, typeText, icon));
    }

    @NotNull
    private LookupElement createLookupElement(final String originPrefix, @NotNull String lookupString, @NotNull String type, @Nullable Icon icon, double priority) {
        return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(originPrefix + lookupString).withPresentableText(lookupString).withTypeText(type).bold().withIcon(icon), priority);
    }

    @NotNull
    private LookupElement createLookupElement(@NotNull String prefix, @NotNull String lookupString, @NotNull String type, @Nullable Icon icon) {
        return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(prefix + lookupString).withPresentableText(lookupString).withTypeText(type).bold().withIcon(icon), PRIORITY);
    }

}
