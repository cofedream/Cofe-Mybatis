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
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.mybatis.dom.model.mix.CRUDMix;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.Optional;

/**
 * 代码完成,无需指向引用
 *
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MapperXmlCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        if (!MybatisUtils.isElementWithMapperXMLFile(position)) {
            return;
        }
        XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(position, XmlAttribute.class);
        if (xmlAttribute == null) {
            return;
        }
        XmlTag xmlTag = PsiTreeUtil.getParentOfType(position, XmlTag.class, true);
        if (xmlTag == null) {
            return;
        }
        parse(StatementAttribute.values(), xmlAttribute).ifPresent(statementAttribute -> statementAttribute.process(xmlTag, result));
    }

    private enum StatementAttribute {
        RESULT_TYPE("resultType") {
            @Override
            void process(XmlTag xmlTag, CompletionResultSet result) {
                CRUDMix CRUDMix = (CRUDMix) DomUtils.getDomElement(xmlTag);
                if (CRUDMix == null) {
                    return;
                }
                // classElement.getIdMethod()
                //         .filter(info -> info.getReturnType() != null)
                //         .map(PsiMethod::getReturnType)
                //         .ifPresent(type -> {
                //             if (PsiTypeUtils.isPrimitiveOrBoxType(type)) {
                //                 result.addAllElements(TypeAliasUtils.getTypeLookupElement(type.getInternalCanonicalText()));
                //             } else if (type instanceof PsiClassReferenceType) {
                //                 PsiJavaCodeReferenceElement reference = ((PsiClassReferenceType) type).getReference();
                //                 String name = reference.getQualifiedName();
                //                 if (StringUtil.isEmpty(name)) {
                //                     return;
                //                 }
                //                 if (PsiTypeUtils.isPrimitiveOrBoxType(type) || PsiTypeUtils.isCollectionOrMapType(type)) {
                //                     result.addAllElements(TypeAliasUtils.getTypeLookupElement(name));
                //                 } else {
                //                     result.addElement(LookupElementBuilder.create(name));
                //                 }
                //             }
                //         });
                result.stopHere();
            }
        };

        private String value;

        StatementAttribute(String attributeValue) {
            this.value = attributeValue;
        }

        abstract void process(XmlTag xmlTag, CompletionResultSet resultSet);

        public String getValue() {
            return value;
        }

    }

    /**
     * 解析标签
     *
     * @param targetEnums  目标属性枚举
     * @param xmlAttribute 需要解析的属性
     * @return 解析结果
     */
    private static Optional<StatementAttribute> parse(@NotNull StatementAttribute[] targetEnums, @NotNull XmlAttribute xmlAttribute) {
        for (StatementAttribute attribute : targetEnums) {
            if (attribute.getValue().equals(xmlAttribute.getName())) {
                return Optional.of(attribute);
            }
        }
        return Optional.empty();
    }
}
