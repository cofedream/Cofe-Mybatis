package tk.cofe.plugin.mybatis.completion;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.constants.Empty;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;
import tk.cofe.plugin.mybatis.util.PsiTypeUtils;
import tk.cofe.plugin.mybatis.util.StringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * XML 文件中的SQL 参数完成
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class XmlSqlParameterCompletionContributor extends CompletionContributor {

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
        InjectedLanguageManager manager = InjectedLanguageManager.getInstance(position.getProject());

        PsiFile xmlFile = manager.getTopLevelFile(position);
        if (!PsiMybatisUtils.isMapperXmlFile(xmlFile)) {
            return;
        }
        if (isSupport(parameters)) {
            ClassElement classElement = DomUtils.getTargetElement(xmlFile.findElementAt(manager.injectedToHost(position, position.getTextOffset())), ClassElement.class);
            if (classElement != null) {
                JavaPsiService javaPsiService = JavaPsiService.getInstance(position.getProject());
                javaPsiService.findPsiMethod(classElement).ifPresent(psiMethod -> process(javaPsiService, psiMethod, result, getPrefix(result)));
            }
        }
    }

    private void process(@NotNull JavaPsiService javaPsiService, @NotNull PsiMethod psiMethod, @NotNull CompletionResultSet result, @NotNull String[] prefixs) {
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
                        process(javaPsiService, ((PsiClassReferenceType) psiParameters[0].getType()), result, prefixs);
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
            if (psiParameter.getType() instanceof PsiClassReferenceType) {
                // todo 判断是否自定义类型
                PsiClassReferenceType psiClassReferenceType = ((PsiClassReferenceType) psiParameter.getType());
                for (int i = 1; i < prefixs.length; i++) {
                    String prefix = prefixs[i];
                    String qualifiedName = psiClassReferenceType.getReference().getQualifiedName();
                    List<PsiMember> psiMembers = javaPsiService.findPsiClass(qualifiedName).map(psiClass -> {
                        List<PsiMember> members = new ArrayList<>();
                        members.addAll(Arrays.asList(psiClass.getAllFields()));
                        members.addAll(Arrays.asList(psiClass.getAllMethods()));
                        return members;
                    }).orElse(Collections.emptyList());
                    for (PsiMember psiMember : psiMembers) {
                        if (psiMember instanceof PsiField) {
                            if (prefix.equals(psiMember.getName())) {
                                if (((PsiField) psiMember).getType() instanceof PsiClassReferenceType) {
                                    psiClassReferenceType = (PsiClassReferenceType) ((PsiField) psiMember).getType();
                                }
                                break;
                            }
                        }
                    }
                    //javaPsiService.findPsiClass(qualifiedName).filter(psiClass -> {
                    //    for (PsiField field : psiClass.getAllFields()) {
                    //        if (prefix.equals(field.getName())) {
                    //            return field.getType() instanceof PsiClassReferenceType;
                    //        }
                    //    }
                    //    return false;
                    //    PsiMethod[] allMethods = psiClass.getAllMethods();
                    //}).ifPresent(psiClass -> {
                    //    for (PsiField field : psiClass.getAllFields()) {
                    //        if (prefix.equals(field.getName())) {
                    //            if (field.getType() instanceof PsiClassReferenceType) {
                    //                //psiClassReferenceType = ((PsiClassReferenceType) field.getType());
                    //            }
                    //            return;
                    //        }
                    //        //        result.addElement(createLookupElement(field.getName(), field.getType().getPresentableText(), PlatformIcons.FIELD_ICON));
                    //    }
                    //    //    for (PsiMethod method : psiClass.getAllMethods()) {
                    //    //        if (isTargetMethod(method)) {
                    //    //            result.addElement(createLookupElement(processMethodName(method), method.getReturnType().getPresentableText(), getTailText(method), PlatformIcons.METHOD_ICON));
                    //    //        }
                    //    //    }
                    //});
                }
                process(javaPsiService, psiClassReferenceType, result, prefixs);
            }
            //Arrays.stream(psiParameters).collect(Collectors.toMap(k -> {
            //    Annotation.Value value = Annotation.PARAM.getValue(k);
            //    if (value == null) {
            //        return k.getType().getPresentableText();
            //    }
            //    return value;
            //}, v -> {
            //    return v;
            //}));
        }
        //if (prefixs.length == 0) {
        //    // 2.无前缀
        //    // 2.1 参数==1
        //    if (psiParameters.length == 1) {
        //        PsiParameter parameter = psiParameters[0];
        //        Annotation.PARAM.getValue(parameter);
        //        //parameter.getType();
        //    }
        //
        //} else {
        //    // 1.有前缀
        //
        //    // 1.1 参数==1
        //    // 1.2 参数>1
        //}


        // todo 提取为单独方法
        if (prefixs.length == 0) {
            for (int i = 0; i < psiParameters.length; i++) {
                result.addElement(createLookupElement("param" + (i + 1), psiParameters[i].getType().getPresentableText(), AllIcons.Nodes.Class));
            }
        }

        // 2.2 参数>1
        //if (prefixs.length == 0) {
        //process(javaPsiService, psiParameters, result);
        //} else {
        //    // 处理第一个前缀
        //    PsiClassReferenceType prefixReferenceType = getPrefixReferenceType(javaPsiService, psiParameters, prefixs[0]);
        //    // 处理剩余前缀
        //    for (int i = 1; i < prefixs.length && prefixReferenceType != null; i++) {
        //        prefixReferenceType = getPrefixReferenceType(javaPsiService, prefixReferenceType, prefixs[i]);
        //    }
        //    if (prefixReferenceType != null) {
        //        String qualifiedName = prefixReferenceType.getReference().getQualifiedName();
        //        javaPsiService.findPsiClass(qualifiedName).ifPresent(psiClass -> {
        //            String tailText = getTailText(psiClass);
        //            for (PsiField field : psiClass.getAllFields()) {
        //                String join = getLookupString(prefixs, field.getName());
        //                result.addElement(createLookupElement(join, field.getType().getPresentableText(), field.getName(), tailText, PlatformIcons.FIELD_ICON));
        //            }
        //            for (PsiMethod method : psiClass.getAllMethods()) {
        //                if (isTargetMethod(method)) {
        //                    String methodName = processMethodName(method);
        //                    result.addElement(createLookupElement(getLookupString(prefixs, methodName), method.getReturnType().getPresentableText(), methodName, getTailText(method), PlatformIcons.METHOD_ICON));
        //                }
        //            }
        //        });
        //    }
        //}
        result.stopHere();
    }

    private void process(@NotNull JavaPsiService javaPsiService, PsiParameter[] psiParameters, @NotNull CompletionResultSet result) {
        for (int i = 0; i < psiParameters.length; i++) {
            PsiParameter parameter = psiParameters[i];
            PsiType parameterType = parameter.getType();
            String typeText = parameterType.getPresentableText();
            String annotationValue = getParamAnnotationValue(parameter);
            if (annotationValue != null) {
                result.addElement(createLookupElement(annotationValue, typeText, AllIcons.Nodes.Parameter));
                result.addElement(createLookupElement("param" + (i + 1), typeText, null, AllIcons.Nodes.Parameter, PRIORITY - i));
            } else {
                if (PsiTypeUtils.isCustomType(parameterType)) {
                    // todo 处理集合
                    result.addElement(createLookupElement("param" + (i + 1), typeText, null, AllIcons.Nodes.Parameter, PRIORITY - i));
                } else {
                    // 自定义类型
                    if (parameterType instanceof PsiClassReferenceType) {
                        process(javaPsiService, ((PsiClassReferenceType) parameterType), result, new String[0]);
                    }
                }
            }
        }
    }

    private void process(@NotNull JavaPsiService javaPsiService, @NotNull PsiClassReferenceType referenceType, @NotNull CompletionResultSet result, String[] prefixs) {
        String prefiex = String.join(".", prefixs);
        String qualifiedName = referenceType.getReference().getQualifiedName();
        javaPsiService.findPsiClass(qualifiedName).ifPresent(psiClass -> {
            for (PsiField field : psiClass.getAllFields()) {
                String name = field.getName();
                if (name != null) {
                    if (prefiex.length() != 0) {
                        name = prefiex + "." + name;
                    }
                    result.addElement(createLookupElement(name, field.getType().getPresentableText(), PlatformIcons.FIELD_ICON));
                }
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
                if (PsiTypeUtils.isCustomType(type)) {
                    return ((PsiClassReferenceType) type);
                }
            }
        } else {
            for (PsiParameter parameter : psiParameters) {
                String annotationValue = getParamAnnotationValue(parameter);
                PsiType type = parameter.getType();
                if (PsiTypeUtils.isCustomType(type)) {
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
        return javaPsiService.findPsiClass((prefixReferenceType).getReference().getQualifiedName()).map(psiClass -> {
            for (PsiField field : psiClass.getAllFields()) {
                if (referenceKey.equals(field.getName()) && PsiTypeUtils.isCustomType(field.getType())) {
                    return ((PsiClassReferenceType) field.getType());
                }
            }
            for (PsiMethod method : psiClass.getAllMethods()) {
                if (isTargetMethod(method, referenceKey) && PsiTypeUtils.isCustomType(method.getReturnType())) {
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
        PsiAnnotation param = parameter.getAnnotation(Annotation.PARAM.getQualifiedName());
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
