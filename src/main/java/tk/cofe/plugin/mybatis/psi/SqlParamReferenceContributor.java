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

package tk.cofe.plugin.mybatis.psi;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.CompletionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

/**
 * SQL 提供参考
 *
 * @author : zhengrf
 * @date : 2019-10-20
 */
public class SqlParamReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlTokenType.XML_DATA_CHARACTERS), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull final ProcessingContext context) {
                String text = element.getText();
                int startIndex = text.indexOf("#{");
                int endIndex = text.indexOf("}");
                if (startIndex > -1 && startIndex < endIndex
                        && MybatisUtils.isElementWithMapperXMLFile(element)) {
                    String prefix = text.substring(startIndex + 2, endIndex);
                    if (StringUtil.isNotEmpty(prefix)) {
                        String[] prefixArr = prefix.split("\\.");
                        PsiReferenceBase.Immediate<PsiElement> referenceElement = new PsiReferenceBase.Immediate<PsiElement>(element, DomUtils.getDomElement(element, ClassElement.class)
                                .flatMap(ClassElement::getIdMethod).map(psiMethod -> {
                                    PsiElement prefixElement = CompletionUtils.getPrefixElement(prefixArr, ((PsiParameter[]) psiMethod.getParameters()));
                                    return prefixElement;
                                }).orElse(null));
                        return new PsiReference[] {referenceElement};
                    }
                }
                return new PsiReference[0];
            }
        });
    }
}
