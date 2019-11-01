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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
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
import tk.cofe.plugin.common.bundle.MyBatisBundle;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.provider.VariantsProvider;
import tk.cofe.plugin.mybatis.util.CompletionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;
import tk.cofe.plugin.mybatis.util.PsiTypeUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author : zhengrf
 * @date : 2019-08-10
 */
public class TestConverter extends ResolvingConverter.StringConverter implements VariantsProvider<Set<String>> {
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

    @Override
    public String fromString(final String s, final ConvertContext context) {
        if (StringUtil.isEmpty(s)) {
            return null;
        }
        try {
            Node node = (Node) Ognl.parseExpression(s);
            if (node != null) {
                Node child = node.jjtGetChild(node.jjtGetNumChildren() - 1);
                if (child != null) {
                    return node.toString();
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public String getErrorMessage(@Nullable final String s, final ConvertContext context) {
        return MyBatisBundle.message("error.cannot.parse.message", "OGNL Expression", s);
    }

    @NotNull
    @Override
    public Collection<? extends String> getVariants(final ConvertContext context) {
        XmlAttribute xmlAttributeValue = (XmlAttribute) context.getXmlElement();
        if (xmlAttributeValue == null) {
            return Collections.emptySet();
        }
        final String originPrefix = CompletionUtils.getPrefixStr(xmlAttributeValue.getValue());
        if (!isSupport(originPrefix)) {
            return Collections.emptySet();
        }
        ClassElement classElement = DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class);
        if (classElement == null) {
            return Collections.emptySet();
        }
        return classElement.getIdMethod()
                .map(method -> provider(getCompletionPrefix(originPrefix), getPrefix(originPrefix), (PsiParameter[]) method.getParameters(), new HashSet<>()))
                .orElse(Collections.emptySet());
    }

    @Override
    public void singleParam(@NotNull final String prefixText, @NotNull final String[] prefixArr, @NotNull final PsiParameter firstParameter, @NotNull final Set<String> res) {
        Annotation.Value value = Annotation.PARAM.getValue(firstParameter);
        if (value == null) {
            // 如果是自定义类型,则读取类字段,如果不是则不做处理使用后续的 param1
            PsiTypeUtils.isCustomType(firstParameter.getType(), psiClassType -> addPsiClassTypeVariants(prefixText, psiClassType, res));
        } else {
            res.add(prefixText + value.getValue());
        }
    }

    @Override
    public void multiParam(@NotNull final String prefixText, @NotNull final String[] prefixArr, @NotNull final PsiParameter[] parameters, @NotNull final Set<String> res) {
        for (PsiParameter psiParameter : parameters) {
            Optional.ofNullable(Annotation.PARAM.getValue(psiParameter)).ifPresent(value -> res.add(value.getValue()));
        }
    }

    @Override
    public void emptyPrefix(@NotNull final String prefixText, @NotNull final String[] prefixArr, @NotNull final PsiParameter[] parameters, @NotNull final Set<String> res) {
        PsiType type = CompletionUtils.getPrefixType(prefixArr[0], parameters);
        // 自定义类类型则取字段和方法
        PsiTypeUtils.isCustomType(type, psiClassType -> addPsiClassTypeVariants(prefixText, CompletionUtils.getPrefixPsiClass(prefixArr, type), res));
    }

    private boolean isSupport(@Nullable String prefix) {
        if (StringUtil.isEmpty(prefix)) {
            return true;
        }
        if (prefix.charAt(prefix.length() - 1) == '.') {
            prefix = prefix + 'a';
        }
        try {
            Ognl.parseExpression(prefix);
        } catch (OgnlException e) {
            return prefix.charAt(prefix.length() - 1) == ' ';
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
        if (StringUtil.isEmpty(prefix)) {
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
        if (StringUtil.isEmpty(prefix)) {
            return new String[0];
        }
        if (prefix.charAt(prefix.length() - 1) == '.') {
            prefix = prefix + 'a';
        }
        Node node;
        try {
            node = (Node) Ognl.parseExpression(prefix);
        } catch (OgnlException e) {
            return new String[0];
        }
        return CompletionUtils.getPrefixArr(getPrefix(node));
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
            if (PsiJavaUtils.notSerialField(field)) {
                res.add(prefix + field.getName());
            }
        }
    }

    private void addMethodsVariants(final String prefix, final Set<String> res, final PsiMethod[] methods) {
        for (PsiMethod psiMethod : methods) {
            if (PsiJavaUtils.isGetMethod(psiMethod) && psiMethod.getReturnType() != null) {
                res.add(prefix + PsiJavaUtils.replaceGetPrefix(psiMethod));
            }
        }
    }
}
