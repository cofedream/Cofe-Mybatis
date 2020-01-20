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

package tk.cofe.plugin.mybatis.inject;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbsp.MbspLanguage;
import tk.cofe.plugin.mybatis.dom.model.attirubte.TestAttribute;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.function.Function;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MbspConditionInject extends BaseInjector {

    @Override
    void inject(@NotNull final MultiHostRegistrar registrar, @NotNull final PsiElement context) {
        //DomUtils.getDomElement(context, TestAttribute.class)
        //        .map(TestAttribute::getTest)
        //        .map(GenericAttributeValue::getXmlAttributeValue)
        //        .ifPresent(xmlAttributeValue -> {
        //            final String text1 = xmlAttributeValue.getText();
        //            registrar.startInjecting(MbspLanguage.INSTANCE)
        //                    .addPlace("#{", "}", (PsiLanguageInjectionHost) xmlAttributeValue, new TextRange(1, text1.length() - 1))
        //                    .doneInjecting();
        //        });
        //DomUtils.getDomElement(context, Foreach.class)
        //        .map(Foreach::getCollection)
        //        .map(GenericAttributeValue::getXmlAttributeValue)
        //        .ifPresent(xmlAttributeValue -> {
        //            final String text1 = xmlAttributeValue.getText();
        //            registrar.startInjecting(MbspLanguage.INSTANCE)
        //                    .addPlace("#{", "}", (PsiLanguageInjectionHost) xmlAttributeValue, new TextRange(1, text1.length() - 1))
        //                    .doneInjecting();
        //        });
        inject(context, TestAttribute.class, TestAttribute::getTest, registrar);
        // todo Collection注入报错
        //inject(context, Foreach.class, Foreach::getCollection, registrar);
    }

    private <T extends DomElement> void inject(final PsiElement context,
                                               final Class<T> requiredClass,
                                               final Function<T, GenericAttributeValue<String>> mapper,
                                               final MultiHostRegistrar registrar) {
        final XmlTag xmlTag = DomUtils.getXmlTag(context);
        if (!DomUtils.isTargetDomElement(xmlTag, requiredClass)) {
            return;
        }
        DomUtils.getDomElement(xmlTag, requiredClass)
                .map(mapper)
                .map(GenericAttributeValue::getXmlAttributeValue)
                .ifPresent(xmlAttributeValue -> {
                    final String text = xmlAttributeValue.getText();
                    registrar.startInjecting(MbspLanguage.INSTANCE)
                            .addPlace("#{", "}", (PsiLanguageInjectionHost) xmlAttributeValue, new TextRange(1, text.length() - 1))
                            .doneInjecting();
                });
    }

    Class<XmlAttributeValue> targetElement() {
        return XmlAttributeValue.class;
    }
}
