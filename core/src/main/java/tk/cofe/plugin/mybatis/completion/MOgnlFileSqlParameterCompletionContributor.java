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
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.*;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.provider.VariantsProvider;
import tk.cofe.plugin.mybatis.util.MybatisXMLUtils;

import javax.swing.*;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * SQL 文件中的SQL 参数完成
 *
 * @author : zhengrf
 * @date : 2019-08-11
 */
public class MOgnlFileSqlParameterCompletionContributor extends CompletionContributor implements VariantsProvider<CompletionResultSet> {

    /**
     * 权重
     */
    private static final double PRIORITY = 100;
    private static final RowIcon PRIVATE_FIELD_ICON = new RowIcon(PlatformIcons.FIELD_ICON, PlatformIcons.PRIVATE_ICON);

    @Override
    public void fillCompletionVariants(@NotNull final CompletionParameters parameters, @NotNull final CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        final PsiElement targetElement = getTargetElement(parameters, result);
        DomUtils.getDomElement(targetElement, ClassElement.class).flatMap(ClassElement::getIdMethod)
                .ifPresent(psiMethod -> {
                    bindTag(result, targetElement);
                    foreachTag(result, targetElement);
                    String prefixText = getPrefixText(parameters.getPosition(), result);
                    provider(prefixText, CompletionUtils.getPrefixArr(prefixText), psiMethod.getParameterList().getParameters(), result);
                });
    }

    /**
     * 处理 &lt;bind&gt; 标签
     */
    private void bindTag(@NotNull final CompletionResultSet result, final PsiElement targetElement) {
        MybatisXMLUtils.getTheBindTagInParents(targetElement).stream()
                .map(bind -> DomUtils.getAttributeValue(bind.getName()))
                .filter(Objects::nonNull)
                .forEach(bind -> result.addElement(createLookupElement(bind, "", null)));
    }

    private void foreachTag(@NotNull final CompletionResultSet result, final PsiElement targetElement) {
        MybatisXMLUtils.getTheForeachTagInParents(targetElement).stream()
                .map(foreach -> DomUtils.getAttributeValue(foreach.getItem()))
                .filter(Objects::nonNull)
                .forEach(bind -> result.addElement(createLookupElement(bind, "", null)));
    }

    @Override
    public void singleParam(final String prefixText, final String[] prefixArr, final PsiParameter firstParameter, final CompletionResultSet result) {
        Annotation.Value value = Annotation.PARAM.getValue(firstParameter);
        if (value == null) {
            // 如果是自定义类型,则读取类字段,如果不是则不做处理使用后续的 param1
            PsiTypeUtils.isCustomType(firstParameter.getType(), psiClassType -> addPsiClassTypeVariants(prefixText, psiClassType, result));
        } else {
            result.addElement(createLookupElement(value.getValue(), firstParameter.getType().getPresentableText(), AllIcons.Nodes.Parameter));
        }
    }

    @Override
    public void multiParam(final String prefixText, final String[] prefixArr, final PsiParameter[] parameters, final CompletionResultSet result) {
        for (PsiParameter psiParameter : parameters) {
            Optional.ofNullable(Annotation.PARAM.getValue(psiParameter)).ifPresent(value -> result.addElement(createLookupElement(value.getValue(), psiParameter.getType().getPresentableText(), AllIcons.Nodes.Parameter)));
        }
    }

    @Override
    public void emptyPrefix(final String prefixText, final String[] prefixArr, final PsiParameter[] parameters, final CompletionResultSet res) {
        PsiType type = CompletionUtils.getPrefixType(prefixArr[0], parameters);
        // 自定义类类型则取字段和方法
        PsiTypeUtils.isCustomType(type, psiClassType -> addPsiClassTypeVariants(prefixText, CompletionUtils.getPrefixPsiClass(prefixArr, type), res));
    }

    @Override
    public void beforeReturn(final String prefixText, final String[] prefixArr, final PsiParameter[] parameters, final CompletionResultSet result) {
        addParamsVariants(result, prefixArr, parameters);
        result.stopHere();
    }

    /**
     * 通过类添加提示
     *
     * @param prefixText
     * @param psiType    Java类类型
     * @param result     结果集
     */
    private void addPsiClassTypeVariants(final String prefixText, @Nullable PsiClassType psiType, CompletionResultSet result) {
        CompletionUtils.getPsiClassTypeVariants(psiType,
                field -> createLookupElement(prefixText, field.getName(), field.getType().getPresentableText(), PsiTypeUtils.isCustomType(field.getType()) ? PlatformIcons.CLASS_ICON : PRIVATE_FIELD_ICON, result::addElement),
                method -> createLookupElement(prefixText, PsiMethodUtils.replaceGetPrefix(method), method.getReturnType().getPresentableText(), PlatformIcons.METHOD_ICON, result::addElement));
    }

    /**
     * 添加 param1-paramn 的提示
     *
     * @param result           结果集
     * @param prefixs          前缀
     * @param methodParameters 方法参数
     */
    private void addParamsVariants(CompletionResultSet result, String[] prefixs, PsiParameter[] methodParameters) {
        // 方法参数大于一个的时候才进行 param1->paramN的提示,否则仅提示类内部字段
        if (prefixs.length != 0 && methodParameters.length <= 1) {
            return;
        }
        for (int i = 0; i < methodParameters.length; i++) {
            result.addElement(createLookupElement("param" + (i + 1), methodParameters[i].getType().getPresentableText(), AllIcons.Nodes.Parameter, methodParameters.length - i));
        }
    }

    private void createLookupElement(final String preFix, @Nullable final String name, final String typeText, final Icon icon, final Consumer<LookupElement> consumer) {
        if (StringUtil.isEmpty(name) || StringUtil.isEmpty(typeText)) {
            return;
        }
        consumer.accept(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(preFix + name).withPresentableText(name).withTypeText(typeText).bold().withIcon(icon), PRIORITY));
    }

    @NotNull
    private LookupElement createLookupElement(String lookupString, String type, @Nullable Icon icon) {
        return createLookupElement(lookupString, type, icon, PRIORITY);
    }

    @NotNull
    private LookupElement createLookupElement(String lookupString, String type, @Nullable Icon icon, double priority) {
        return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(lookupString).withPresentableText(lookupString).withTypeText(type).bold().withIcon(icon), priority);
    }

    PsiElement getTargetElement(final CompletionParameters parameters, final CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        return InjectedLanguageManager.getInstance(position.getProject()).getInjectionHost(position);
    }

    /**
     * 获取前缀,用于值填充
     */
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
