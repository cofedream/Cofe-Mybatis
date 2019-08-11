/*
 * Copyright (C) 2019 cofe
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import ognl.ASTChain;
import ognl.Node;
import ognl.Ognl;
import ognl.OgnlException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.constants.Empty;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.CompletionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;
import tk.cofe.plugin.mybatis.util.PsiTypeUtils;
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : zhengrf
 * @date : 2019-08-10
 */
public class TestConverter extends ResolvingConverter.StringConverter {
    @NotNull
    @Override
    public Collection<? extends String> getVariants(final ConvertContext context) {
        XmlAttribute xmlAttributeValue = (XmlAttribute) context.getInvocationElement().getXmlElement();
        if (xmlAttributeValue == null) {
            return Collections.emptySet();
        }
        String attributeValue = xmlAttributeValue.getValue();
        final String originPrefix = getPrefixStr(attributeValue);
        if (!isSupport(originPrefix)) {
            return Collections.emptySet();
        }
        ClassElement classElement = DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class);
        if (classElement == null) {
            return Collections.emptySet();
        }
        return classElement.getIdMethod().map(method -> {
            PsiParameterList parameterList = method.getParameterList();
            if (parameterList.getParametersCount() == 0) {
                return Collections.<String>emptySet();
            }
            PsiParameter[] parameters = parameterList.getParameters();
            Set<String> res = new HashSet<>();
            String[] prefixs = getPrefix(originPrefix);
            if (prefixs.length == 0) {
                if (parameters.length == 1) {
                    Annotation.Value value = Annotation.PARAM.getValue(parameters[0]);
                    if (value == null) {
                        // 如果是自定义类型,则读取类字段,如果不是则不做处理使用后续的 param1
                        if (PsiTypeUtils.isCustomType(parameters[0].getType()) && parameters[0].getType() instanceof PsiClassType) {
                            addPsiClassTypeVariants(getCompletionPrefix(originPrefix), (PsiClassType) parameters[0].getType(), res);
                        }
                    } else {
                        //result.addElement(createLookupElement(value.getValue(), parameters[0].getType().getPresentableText(), AllIcons.Nodes.Parameter));
                    }
                } else {
                    //for (PsiParameter psiParameter : parameters) {
                    //    Optional.ofNullable(Annotation.PARAM.getValue(psiParameter)).ifPresent(value -> result.addElement(createLookupElement(value.getValue(), psiParameter.getType().getPresentableText(), AllIcons.Nodes.Parameter)));
                    //}
                }
            } else {
                PsiType type = CompletionUtils.getPrefixType(prefixs[0], parameters);
                // 自定义类类型则取字段和方法
                if (type != null && PsiTypeUtils.isCustomType(type)) {
                    addPsiClassTypeVariants(getCompletionPrefix(originPrefix), CompletionUtils.getTargetPsiClass(prefixs, (PsiClassType) type), res);
                }
            }
            return res;
        }).orElse(Collections.emptySet());
    }

    private String getPrefixStr(final String attributeValue) {
        if (StringUtils.isNotBlank(attributeValue)) {
            String[] prefixArr = attributeValue.split("IntellijIdeaRulezzz ");
            if (prefixArr.length > 0) {
                return prefixArr[0];
            }
        }
        return "";
    }

    private boolean isSupport(@Nullable String prefix) {
        if (prefix == null || prefix.trim().length() == 0) {
            return true;
        }
        if (prefix.charAt(prefix.length() - 1) == '.') {
            prefix = prefix + 'a';
        }
        try {
            Ognl.parseExpression(prefix);
        } catch (OgnlException e) {
            if (prefix.charAt(prefix.length() - 1) == ' ') {
                return true;
            }
            return false;
        }
        for (int i = prefix.length() - 2; i > 0; i--) {
            char charAt = prefix.charAt(i);
            if (charAt == '=' || charAt == '!' || charAt == '>' || charAt == '<') {
                return false;
            }
            if (charAt == '.' || charAt == ' ') {
                return true;
            }
        }
        return true;
    }

    private String getCompletionPrefix(@Nullable String prefix) {
        if (StringUtils.isBlank(prefix)) {
            return "";
        }
        int pointIndex = prefix.lastIndexOf(".");
        int blankIndex = prefix.lastIndexOf(" ");
        if (pointIndex > blankIndex) {
            return prefix.substring(0, pointIndex + 1);
        }
        if (prefix.length() > blankIndex) {
            return prefix.substring(0, blankIndex + 1);
        } else {
            return prefix;
        }
    }

    /**
     * 获取前缀
     */
    @NotNull
    private String[] getPrefix(@NotNull String prefix) {
        if (StringUtils.isBlank(prefix)) {
            return Empty.Array.STRING;
        }
        if (prefix.charAt(prefix.length() - 1) == '.') {
            prefix = prefix + 'a';
        }
        Node node;
        try {
            node = (Node) Ognl.parseExpression(prefix);
        } catch (OgnlException e) {
            return Empty.Array.STRING;
        }
        prefix = getPrefix(node);
        if (!prefix.contains(".")) {
            return Empty.Array.STRING;
        }
        String substring = prefix.substring(0, prefix.lastIndexOf("."));
        return substring.split("\\.");
    }

    private static String getPrefix(Node node) {
        if (node instanceof ASTChain) {
            return node.toString();
        }
        int i = node.jjtGetNumChildren();
        if (i > 1) {
            return getPrefix(node.jjtGetChild(i - 1));
        }
        return node.toString();
    }

    private void addPsiClassTypeVariants(final String prefix, @Nullable final PsiClassType psiType, @NotNull final Set<String> res) {
        if (psiType == null) {
            return;
        }
        PsiClass psiClass = psiType.resolve();
        if (psiClass == null) {
            return;
        }
        if (psiClass.isEnum()) {
            addMethodsVariants(prefix, res, psiClass.getMethods());
        } else {
            addFieldsVariants(prefix, res, psiClass.getAllFields());
            addMethodsVariants(prefix, res, psiClass.getAllMethods());
        }
    }

    private void addFieldsVariants(final String prefix, final Set<String> res, final PsiField[] fields) {
        for (PsiField field : fields) {
            if (CompletionUtils.isTargetField(field)) {
                res.add(prefix + field.getName());
            }
        }
    }

    private void addMethodsVariants(final String prefix, final Set<String> res, final PsiMethod[] methods) {
        for (PsiMethod psiMethod : methods) {
            if (CompletionUtils.isTargetMethod(psiMethod) && psiMethod.getReturnType() != null) {
                res.add(prefix + PsiJavaUtils.processGetMethodName(psiMethod));
            }
        }
    }

}
