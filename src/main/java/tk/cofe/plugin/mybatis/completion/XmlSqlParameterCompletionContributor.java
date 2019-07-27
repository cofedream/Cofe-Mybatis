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
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMember;
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
import tk.cofe.plugin.mybatis.service.JavaPsiService;
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
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class XmlSqlParameterCompletionContributor extends CompletionContributor {

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
        PsiElement position = parameters.getPosition();
        InjectedLanguageManager manager = InjectedLanguageManager.getInstance(position.getProject());
        PsiFile xmlFile = manager.getTopLevelFile(position);
        if (!PsiMybatisUtils.isMapperXmlFile(xmlFile)) {
            return;
        }
        if (isSupport(parameters)) {
            PsiElement elementAt = xmlFile.findElementAt(manager.injectedToHost(position, position.getTextOffset()));
            ClassElement classElement = DomUtils.getTargetElement(elementAt, ClassElement.class);
            if (classElement != null) {
                JavaPsiService.getInstance(position.getProject()).findPsiMethod(classElement)
                        .ifPresent(psiMethod -> addPsiParamaterVariants(getPrefix(result), psiMethod.getParameterList().getParameters(), result));
            }
        }
    }

    private void addPsiParamaterVariants(@NotNull String[] prefixs, final PsiParameter[] parameters, @NotNull CompletionResultSet result) {
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
                        addPsiClassTypeVariants(prefixs, (PsiClassType) parameters[0].getType(), result);
                    }
                } else {
                    result.addElement(createLookupElement(value.getValue(), parameters[0].getType().getPresentableText(), AllIcons.Nodes.Parameter));
                }
            } else {
                for (PsiParameter psiParameter : parameters) {
                    Optional.ofNullable(Annotation.PARAM.getValue(psiParameter)).ifPresent(value -> result.addElement(createLookupElement(value.getValue(), psiParameter.getType().getPresentableText(), AllIcons.Nodes.Parameter)));
                }
            }
        } else {
            PsiType type = getPrefixType(prefixs[0], parameters);
            // 自定义类类型则取字段和方法
            if (type != null && PsiTypeUtils.isCustomType(type)) {
                addPsiClassTypeVariants(prefixs, getTargetPsiClass(prefixs, (PsiClassType) type), result);
            }
        }
        addParamsVariants(result, prefixs, parameters);
        result.stopHere();
    }

    /**
     * 获取前缀对应的类型
     * @param prefix        前缀
     * @param psiParameters 参数数组
     * @return 前缀对应的类型
     */
    @Nullable
    private PsiType getPrefixType(@NotNull final String prefix, @NotNull final PsiParameter[] psiParameters) {
        if (psiParameters.length == 1) {
            if (PsiTypeUtils.isCustomType(psiParameters[0].getType())) {
                Annotation.Value value = Annotation.PARAM.getValue(psiParameters[0]);
                if (value == null) {
                    return getTargetPsiType(prefix, psiParameters[0].getType());
                } else if (value.getValue().equals(prefix)) {
                    return psiParameters[0].getType();
                }
            }
        } else {
            for (int i = 0; i < psiParameters.length; i++) {
                Annotation.Value value = Annotation.PARAM.getValue(psiParameters[i]);
                if ((value != null && prefix.equals(value.getValue())) || prefix.equals("param" + (i + 1))) {
                    return psiParameters[i].getType();
                }
            }
        }
        return null;
    }

    /**
     * 根据前缀获取目标类中字段的类型或方法的返回值类型
     * @param psiClass 类对象
     * @param prefixs  前缀
     */
    @Nullable
    private PsiClassType getTargetPsiClass(final @NotNull String[] prefixs, @Nullable final PsiClassType psiClass) {
        PsiClassType target = psiClass;
        for (int i = 1; i < prefixs.length; i++) {
            if (target == null) {
                return null;
            }
            target = getTargetPsiClass(prefixs[i], target);
        }
        return target;
    }

    /**
     * 根据前缀获取目标类中字段的类型或方法的返回值类型
     * @param prefix  前缀
     * @param psiType 类对象
     */
    @Nullable
    private PsiClassType getTargetPsiClass(@NotNull String prefix, @NotNull PsiClassType psiType) {
        PsiClass psiClass = psiType.resolve();
        if (psiClass == null) {
            return null;
        }
        for (PsiMember psiMember : psiClass.getAllMethods()) {
            // 字段名与前缀匹配 且 为自定义类型
            if (prefix.equals(psiMember.getName()) && PsiTypeUtils.isCustomType(((PsiField) psiMember).getType())) {
                return (PsiClassType) ((PsiField) psiMember).getType();
            }
        }
        // 字段名和前缀匹配
        for (PsiMember psiMember : psiClass.getAllMethods()) {
            if (prefix.equals(processGetMethodName(((PsiMethod) psiMember)))) {
                PsiType returnType = ((PsiMethod) psiMember).getReturnType();
                // 返回值不为 null 且 为自定义类型
                if (returnType != null && PsiTypeUtils.isCustomType(returnType)) {
                    return (PsiClassType) returnType;
                }
            }
        }
        return null;
    }


    /**
     * 根据前缀获取目标类中字段的类型或方法的返回值类型
     * @param prefix  前缀
     * @param psiType 类对象
     */
    @Nullable
    private PsiType getTargetPsiType(@NotNull String prefix, @Nullable PsiType psiType) {
        if (!(psiType instanceof PsiClassType)) {
            return null;
        }
        final PsiClass psiClass = ((PsiClassType) psiType).resolve();
        if (psiClass == null) {
            return null;
        }
        for (PsiMember psiMember : psiClass.getAllMethods()) {
            // 字段名与前缀匹配 且 为自定义类型
            if (prefix.equals(psiMember.getName()) && PsiTypeUtils.isCustomType(((PsiField) psiMember).getType())) {
                return ((PsiField) psiMember).getType();
            }
        }
        // 字段名和前缀匹配
        for (PsiMember psiMember : psiClass.getAllMethods()) {
            if (prefix.equals(processGetMethodName(((PsiMethod) psiMember))) && isTargetMethod(((PsiMethod) psiMember))) {
                return ((PsiMethod) psiMember).getReturnType();
            }
        }
        return null;
    }

    /**
     * 判断是否为 getXXX 函数
     * @param method 方法名
     */
    private boolean isTargetMethod(@NotNull PsiMethod method) {
        return PsiJavaUtils.isPublicMethod(method) && !PsiJavaUtils.isVoidMethod(method) && !PsiJavaUtils.isNativeMethod(method) && PsiJavaUtils.isGetMethod(method);
    }

    /**
     * 处理 getAaaBbb 方法名称
     * @param method java方法
     * @return getAaaBbb->aaaBbb
     */
    @Nullable
    private String processGetMethodName(@NotNull PsiMethod method) {
        String methodName = method.getName();
        if (methodName.length() == 3) {
            return null;
        }
        char first = Character.toLowerCase(methodName.charAt(3));
        if (methodName.length() > 4) {
            return first + methodName.substring(4);
        }
        return String.valueOf(first);
    }

    /**
     * 获取前缀
     */
    @NotNull
    private String[] getPrefix(@NotNull CompletionResultSet result) {
        String prefix = result.getPrefixMatcher().getPrefix();
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
     * @param prefixs 前缀
     * @param psiType Java类类型
     * @param result  结果集
     */
    private void addPsiClassTypeVariants(String[] prefixs, @Nullable PsiClassType psiType, @NotNull CompletionResultSet result) {
        if (psiType == null) {
            return;
        }
        PsiClass psiClass = psiType.resolve();
        if (psiClass == null) {
            return;
        }
        String prefiex = String.join(".", prefixs);
        if (psiClass.isEnum()) {
            addMethodsVariants(prefiex, psiClass.getMethods(), result);
        } else {
            addFieldsVariants(prefiex, psiClass.getAllFields(), result);
            addMethodsVariants(prefiex, psiClass.getAllMethods(), result);
        }
    }

    private void addFieldsVariants(final String prefiex, final PsiField[] fields, @NotNull final CompletionResultSet result) {
        for (PsiField field : fields) {
            if (isTargetField(field)) {
                createLookupElement(prefiex, field.getName(), field.getType().getPresentableText(), PsiTypeUtils.isCustomType(field.getType()) ? PlatformIcons.CLASS_ICON : PRIVATE_FIELD_ICON, result::addElement);
            }
        }
    }

    private void addMethodsVariants(final String prefiex, final PsiMethod[] methods, @NotNull final CompletionResultSet result) {
        for (PsiMethod method : methods) {
            if (isTargetMethod(method) && method.getReturnType() != null) {
                createLookupElement(prefiex, processGetMethodName(method), method.getReturnType().getPresentableText(), PlatformIcons.METHOD_ICON, result::addElement);
            }
        }
    }

    /**
     * 判断是否为目标字段
     * @param psiField 字段
     */
    private boolean isTargetField(@NotNull PsiField psiField) {
        return !"serialVersionUID".equals(psiField.getName());
    }

    /**
     * 添加 param1-paramn 的提示
     * @param result           结果集
     * @param prefixs          前缀
     * @param methodParameters 方法参数
     */
    private void addParamsVariants(@NotNull CompletionResultSet result, @NotNull String[] prefixs, PsiParameter[] methodParameters) {
        // 方法参数大于一个的时候才进行 param1->paramN的提示,否则仅提示类内部字段
        if (prefixs.length != 0 && methodParameters.length <= 1) {
            return;
        }
        for (int i = 0; i < methodParameters.length; i++) {
            result.addElement(createLookupElement("param" + (i + 1), methodParameters[i].getType().getPresentableText(), AllIcons.Nodes.Parameter, methodParameters.length - i));
        }
    }

    /**
     * 创建提示
     */
    private void createLookupElement(@Nullable final String prefiex, @Nullable final String name, final String typeText, final Icon icon, final Consumer<LookupElement> consumer) {
        if (name == null || typeText == null) {
            return;
        }
        String lookupString = name;
        if (prefiex != null && prefiex.length() != 0) {
            lookupString = prefiex + "." + lookupString;
        }
        consumer.accept(createLookupElement(lookupString, typeText, icon));
    }

    @NotNull
    private LookupElement createLookupElement(@NotNull String lookupString, @NotNull String type, @Nullable Icon icon, double priority) {
        return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(lookupString).withTypeText(type).bold().withIcon(icon), priority);
    }

    @NotNull
    private LookupElement createLookupElement(@NotNull String lookupString, @NotNull String type, @Nullable Icon icon) {
        return createLookupElement(lookupString, type, icon, PRIORITY);
    }

}
