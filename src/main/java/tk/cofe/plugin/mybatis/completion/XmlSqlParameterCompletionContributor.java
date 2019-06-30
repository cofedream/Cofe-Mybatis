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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.constants.Empty;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.description.model.tag.DynamicSql;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;
import tk.cofe.plugin.mybatis.util.PsiTypeUtils;
import tk.cofe.plugin.mybatis.util.StringUtils;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
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
    private static final double PRIORITY = 20;
    /**
     * 尾标题
     */
    private static final String TAIL_TEXT = "                                        ";
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
            DynamicSql targetElement = DomUtils.getTargetElement(elementAt, DynamicSql.class);
            ClassElement classElement = DomUtils.getTargetElement(elementAt, ClassElement.class);
            if (classElement != null) {
                JavaPsiService javaPsiService = JavaPsiService.getInstance(position.getProject());
                javaPsiService.findPsiMethod(classElement).ifPresent(psiMethod -> process(psiMethod, result, getPrefix(result)));
            }
        }
    }

    private void process(@NotNull PsiMethod psiMethod, @NotNull CompletionResultSet result, @NotNull String[] prefixs) {
        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        if (psiParameters.length == 0) {
            return;
        }
        // 根据 paramters 和 prefix 获取元素

        if (prefixs.length == 0) {
            if (psiParameters.length == 1) {
                Annotation.Value value = Annotation.PARAM.getValue(psiParameters[0]);
                if (value == null) {
                    // 如果是自定义类型,则读取类字段,如果不是则不做处理使用后续的 param1
                    if (PsiTypeUtils.isCustomType(psiParameters[0].getType()) && psiParameters[0].getType() instanceof PsiClassReferenceType) {
                        process(prefixs, ((PsiClassReferenceType) psiParameters[0].getType()).resolve(), result);
                    }
                } else {
                    result.addElement(createLookupElement(value.getValue(), psiParameters[0].getType().getPresentableText(), AllIcons.Nodes.Parameter));
                }
            } else {
                for (PsiParameter psiParameter : psiParameters) {
                    Optional.ofNullable(Annotation.PARAM.getValue(psiParameter)).ifPresent(value -> result.addElement(createLookupElement(value.getValue(), psiParameter.getType().getPresentableText(), AllIcons.Nodes.Parameter)));
                }
            }
        } else {
            Map<String, PsiParameter> psiParameterMap = new HashMap<>();
            for (int i = 0; i < psiParameters.length; i++) {
                Annotation.Value value = Annotation.PARAM.getValue(psiParameters[i]);
                if (value != null) {
                    psiParameterMap.put(value.getValue(), psiParameters[i]);
                }
                psiParameterMap.put("param" + (i + 1), psiParameters[i]);
            }
            PsiParameter psiParameter = psiParameterMap.get(prefixs[0]);
            // 自定义类类型则取字段和方法
            if (PsiTypeUtils.isCustomType(psiParameter.getType())) {
                process(prefixs, getTargetPsiClass(prefixs, ((PsiClassReferenceType) psiParameter.getType()).resolve()), result);
            }
        }
        addParamsVariants(result, prefixs, psiParameters);
        result.stopHere();
    }

    /**
     * 根据前缀获取目标类中字段的类型或方法的返回值类型
     * @param psiClass 类对象
     * @param prefixs  前缀
     */
    @Nullable
    private PsiClass getTargetPsiClass(final @NotNull String[] prefixs, @Nullable final PsiClass psiClass) {
        if (psiClass == null) {
            return null;
        }
        PsiClass target = psiClass;
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
     * @param prefix   前缀
     * @param psiClass 类对象
     */
    @Nullable
    private PsiClass getTargetPsiClass(@NotNull String prefix, @NotNull PsiClass psiClass) {
        for (PsiMember psiMember : psiClass.getAllMethods()) {
            // 字段名与前缀匹配 且 为自定义类型
            if (prefix.equals(psiMember.getName()) && PsiTypeUtils.isCustomType(((PsiField) psiMember).getType())) {
                return ((PsiClassReferenceType) ((PsiField) psiMember).getType()).resolve();
            }
        }
        // 字段名和前缀匹配
        for (PsiMember psiMember : psiClass.getAllMethods()) {
            if (prefix.equals(processGetMethodName(((PsiMethod) psiMember)))) {
                PsiType returnType = ((PsiMethod) psiMember).getReturnType();
                // 返回值不为 null 且 为自定义类型
                if (returnType != null && PsiTypeUtils.isCustomType(returnType)) {
                    return ((PsiClassReferenceType) returnType).resolve();
                }
            }
        }
        return null;
    }

    /**
     * 添加 param1-paramn 的提示
     * @param result           结果集
     * @param prefixs          前缀
     * @param methodParameters 方法参数
     */
    private void addParamsVariants(@NotNull CompletionResultSet result, @NotNull String[] prefixs, PsiParameter[] methodParameters) {
        if (prefixs.length == 0) {
            for (int i = 0; i < methodParameters.length; i++) {
                result.addElement(createLookupElement("param" + (i + 1), methodParameters[i].getType().getPresentableText(), AllIcons.Nodes.Parameter, methodParameters.length - i));
            }
        }
    }

    /**
     * 通过类添加提示
     * @param prefixs  前缀
     * @param psiClass Java类
     * @param result   结果集
     */
    private void process(String[] prefixs, @Nullable PsiClass psiClass, @NotNull CompletionResultSet result) {
        if (psiClass == null) {
            return;
        }
        String prefiex = String.join(".", prefixs);
        for (PsiField field : psiClass.getAllFields()) {
            createLookupElement(prefiex, field.getName(), field.getType().getPresentableText(), PsiTypeUtils.isCustomType(field.getType()) ? PlatformIcons.CLASS_ICON : PRIVATE_FIELD_ICON, result::addElement);
        }
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (isTargetMethod(method)) {
                createLookupElement(prefiex, processGetMethodName(method), method.getReturnType().getPresentableText(), PlatformIcons.METHOD_ICON, result::addElement);
            }
        }
    }

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
                return true;
            }
        }
        return false;
    }

    @NotNull
    private LookupElement createLookupElement(@NotNull String lookupString, @NotNull String type, @Nullable Icon icon, double priority) {
        return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(lookupString).withTypeText(type).appendTailText(TAIL_TEXT, true).bold().withIcon(icon), priority);
    }

    @NotNull
    private LookupElement createLookupElement(@NotNull String lookupString, @NotNull String type, @Nullable Icon icon) {
        return createLookupElement(lookupString, type, icon, PRIORITY);
    }

}
