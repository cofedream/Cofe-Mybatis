package tk.cofedream.plugin.mybatis.completion;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.util.PlatformIcons;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofedream.plugin.mybatis.enums.JavaTypeEnum;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;
import tk.cofedream.plugin.mybatis.service.MapperService;
import tk.cofedream.plugin.mybatis.utils.DomUtils;
import tk.cofedream.plugin.mybatis.utils.EmptyUtil;
import tk.cofedream.plugin.mybatis.utils.StringUtils;

import javax.swing.*;

/**
 * 代码完成,无需指向引用
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class SqlParameterCompletionContributor extends CompletionContributor {

    /**
     * 权重
     */
    private static final double PRIORITY = 999.0;
    /**
     * 尾标题
     */
    private static final String TAIL_TEXT = "                                        ";

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        PsiElement position = parameters.getPosition();
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(position.getProject());
        PsiFile xmlFile = injectedLanguageManager.getTopLevelFile(position);
        if (!MapperService.isMapperXmlFile(xmlFile)) {
            return;
        }
        if (isSupport(parameters)) {
            PsiElement elementAt = xmlFile.findElementAt(injectedLanguageManager.injectedToHost(position, position.getTextOffset()));
            ClassElement classElement = DomUtils.getTargetElement(elementAt, ClassElement.class);
            if (classElement != null) {
                JavaPsiService javaPsiService = JavaPsiService.getInstance(position.getProject());
                javaPsiService.findMethod(classElement).ifPresent(psiMethod -> process(javaPsiService, psiMethod, result, getPrefix(result)));
            }
        }
    }

    private void process(@NonNull JavaPsiService javaPsiService, @NotNull PsiMethod psiMethod, @NotNull CompletionResultSet result, @NotNull String[] prefixArr) {
        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        //if (prefixArr.length == 0) {
            process(javaPsiService, psiParameters, result);
        //} else {
        //    // 处理第一个前缀
        //    PsiClassReferenceType prefixReferenceType = getPrefixReferenceType(javaPsiService, psiParameters, prefixArr[0]);
        //    // 处理剩余前缀
        //    for (int i = 1; i < prefixArr.length && prefixReferenceType != null; i++) {
        //        prefixReferenceType = getPrefixReferenceType(javaPsiService, prefixReferenceType, prefixArr[i]);
        //    }
        //    if (prefixReferenceType != null) {
        //        String qualifiedName = prefixReferenceType.getReference().getQualifiedName();
        //        javaPsiService.getPsiClass(qualifiedName).ifPresent(psiClass -> {
        //            String tailText = getTailText(psiClass);
        //            for (PsiField field : psiClass.getAllFields()) {
        //                String join = getLookupString(prefixArr, field.getName());
        //                result.addElement(createLookupElement(join, field.getType().getPresentableText(), field.getName(), tailText, PlatformIcons.FIELD_ICON));
        //            }
        //            for (PsiMethod method : psiClass.getAllMethods()) {
        //                if (isTargetMethod(method)) {
        //                    String methodName = processMethodName(method);
        //                    result.addElement(createLookupElement(getLookupString(prefixArr, methodName), method.getReturnType().getPresentableText(), methodName, getTailText(method), PlatformIcons.METHOD_ICON));
        //                }
        //            }
        //        });
        //    }
        //}
        result.stopHere();
    }

    private void process(@NonNull JavaPsiService javaPsiService, PsiParameter[] psiParameters, @NotNull CompletionResultSet result) {
        for (int i = 0; i < psiParameters.length; i++) {
            PsiParameter parameter = psiParameters[i];
            PsiType parameterType = parameter.getType();
            String typeText = parameterType.getPresentableText();
            String annotationValue = getParamAnnotationValue(parameter);
            if (annotationValue != null) {
                result.addElement(createLookupElement(annotationValue, typeText, PlatformIcons.PARAMETER_ICON));
                result.addElement(createLookupElement("param" + (i + 1), typeText, null, PlatformIcons.PARAMETER_ICON, PRIORITY - i));
            } else {
                if (JavaTypeEnum.parse(parameterType) == JavaTypeEnum.Custom) {
                    // todo 处理集合
                    result.addElement(createLookupElement("param" + (i + 1), typeText, null, PlatformIcons.PARAMETER_ICON, PRIORITY - i));
                } else {
                    // 自定义类型
                    if (parameterType instanceof PsiClassReferenceType) {
                        process(javaPsiService, ((PsiClassReferenceType) parameterType), result);
                    }
                }
            }
        }
    }

    private void process(@NotNull JavaPsiService javaPsiService, @NotNull PsiClassReferenceType referenceType, @NotNull CompletionResultSet result) {
        String qualifiedName = referenceType.getReference().getQualifiedName();
        javaPsiService.getPsiClass(qualifiedName).ifPresent(psiClass -> {
            String tailText = getTailText(psiClass);
            for (PsiField field : psiClass.getAllFields()) {
                result.addElement(createLookupElement(field.getName(), field.getType().getPresentableText(), tailText, PlatformIcons.FIELD_ICON));
            }
            for (PsiMethod method : psiClass.getAllMethods()) {
                if (isTargetMethod(method)) {
                    result.addElement(createLookupElement(processMethodName(method), method.getReturnType().getPresentableText(), getTailText(method), PlatformIcons.METHOD_ICON));
                }
            }
        });
    }

    /**
     * 获取引用
     * @param psiParameters 方法参数
     * @param referenceKey  引用Key值
     * @return Class引用
     */
    @Nullable
    private PsiClassReferenceType getPrefixReferenceType(@NotNull JavaPsiService javaPsiService, @NotNull PsiParameter[] psiParameters, @NotNull String referenceKey) {
        Integer prefixNum = getPrefixNum(referenceKey);
        // todo 集合
        if (prefixNum != null) {
            if (psiParameters.length <= prefixNum) {
                PsiType type = psiParameters[prefixNum - 1].getType();
                if (JavaTypeEnum.parse(type) == JavaTypeEnum.Custom) {
                    return ((PsiClassReferenceType) type);
                }
            }
        } else {
            for (PsiParameter parameter : psiParameters) {
                String annotationValue = getParamAnnotationValue(parameter);
                PsiType type = parameter.getType();
                if (JavaTypeEnum.parse(type) == JavaTypeEnum.Custom) {
                    if (StringUtils.isBlank(annotationValue)) {
                        return getPrefixReferenceType(javaPsiService, ((PsiClassReferenceType) type), referenceKey);
                    } else if (referenceKey.equals(annotationValue)) {
                        return ((PsiClassReferenceType) type);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取引用
     * @param prefixReferenceType 类引用参数
     * @param referenceKey        引用Key值
     * @return Class引用
     */
    @Nullable
    private PsiClassReferenceType getPrefixReferenceType(@NotNull JavaPsiService javaPsiService, @NotNull PsiClassReferenceType prefixReferenceType, @NotNull String referenceKey) {
        return javaPsiService.getPsiClass((prefixReferenceType).getReference().getQualifiedName()).map(psiClass -> {
            for (PsiField field : psiClass.getAllFields()) {
                if (referenceKey.equals(field.getName()) && JavaTypeEnum.parse(field.getType()) == JavaTypeEnum.Custom) {
                    return ((PsiClassReferenceType) field.getType());
                }
            }
            for (PsiMethod method : psiClass.getAllMethods()) {
                if (isTargetMethod(method, referenceKey) && JavaTypeEnum.parse(method.getReturnType()) == JavaTypeEnum.Custom) {
                    return ((PsiClassReferenceType) method.getReturnType());
                }
            }
            return null;
        }).orElse(null);
    }

    private boolean isTargetMethod(PsiMethod method) {
        PsiType returnType = method.getReturnType();
        return returnType != null && !returnType.equalsToText("void") && method.getName().startsWith("get") && !method.getModifierList().hasModifierProperty(PsiModifier.NATIVE);
    }

    private boolean isTargetMethod(PsiMethod method, String name) {
        if (!isTargetMethod(method)) {
            return false;
        }
        String methodName = method.getName();
        return name.equals(Character.toLowerCase(methodName.charAt(4)) + methodName.substring(5));
    }

    private String getLookupString(@NotNull String[] prefixArr, @NotNull String postfix) {
        return String.join(".", prefixArr) + "." + postfix;
    }

    @Nullable
    private Integer getPrefixNum(String prefix) {
        if (prefix.startsWith("param")) {
            return Integer.parseInt(prefix.substring(5));
        }
        if (prefix.startsWith("arg")) {
            return Integer.parseInt(prefix.substring(3)) + 1;
        }
        return null;
    }

    /**
     * 从方法获取 构建 LookupElement所需名称
     * @param method java方法
     * @return String
     */
    @NotNull
    private String processMethodName(@NotNull PsiMethod method) {
        String methodName = method.getName();
        char first = Character.toLowerCase(methodName.charAt(3));
        if (methodName.length() > 4) {
            return first + methodName.substring(4);
        }
        return String.valueOf(first);
    }

    @NotNull
    private String getTailText(@NotNull PsiMethod method) {
        return "(from " + method.getName() + "())";
    }

    @NotNull
    private String getTailText(@NotNull PsiClass psiClass) {
        return "(in " + psiClass.getQualifiedName() + ")";
    }

    @Nullable
    private String getParamAnnotationValue(PsiParameter parameter) {
        PsiAnnotation param = parameter.getAnnotation("org.apache.ibatis.annotations.Param");
        if (param != null) {
            return AnnotationUtil.getStringAttributeValue(param, "value");
        }
        return null;
    }

    //@NotNull
    //private String[] getPrefix(@NotNull CompletionParameters parameters) {
    //    // 获取前缀
    //    String text = parameters.getOriginalFile().getText();
    //    // 当前光标的前一个位置
    //    String prifiex = null;
    //    for (int i = parameters.getOffset() - 1; i > 0; i--) {
    //        if (text.charAt(i) == '{') {
    //            prifiex = text.substring(i + 1, parameters.getOffset());
    //            break;
    //        }
    //    }
    //    if (prifiex == null) {
    //        return EmptyUtil.Array.STRING;
    //    }
    //    int indexOf = prifiex.lastIndexOf(".");
    //    if (indexOf > 0) {
    //        return prifiex.substring(0, indexOf).split("\\.");
    //    }
    //    return EmptyUtil.Array.STRING;
    //}

    @NotNull
    private String[] getPrefix(@NotNull CompletionResultSet result) {
        String prefix = result.getPrefixMatcher().getPrefix();
        if (StringUtils.isBlank(prefix)) {
            return EmptyUtil.Array.STRING;
        }
        return prefix.split("\\.");
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
    private LookupElement createLookupElement(@NotNull String lookupString, @NotNull String type, @Nullable String tailText, @Nullable Icon icon, double priority) {
        LookupElementBuilder builder = LookupElementBuilder.create(lookupString).withTypeText(type).appendTailText(tailText == null ? TAIL_TEXT : tailText, true).bold();
        if (icon != null) {
            builder = builder.withIcon(icon);
        }
        return PrioritizedLookupElement.withPriority(builder, priority);
    }

    @NotNull
    private LookupElement createLookupElement(@NotNull String lookupString, @NotNull String type, @Nullable String tailText, @Nullable Icon icon) {
        return createLookupElement(lookupString, type, tailText, icon, PRIORITY);
    }

    @NotNull
    private LookupElement createLookupElement(@NotNull String lookupString, @NotNull String type, @Nullable Icon icon) {
        return createLookupElement(lookupString, type, null, icon);
    }

    private LookupElement createLookupElement(@NotNull String lookupString, @NotNull String typeText, @NotNull String presentText, String tailText, @NotNull Icon icon) {
        return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(lookupString).withTypeText(typeText).withPresentableText(presentText).appendTailText(tailText, true).withIcon(icon), PRIORITY);
    }

}
