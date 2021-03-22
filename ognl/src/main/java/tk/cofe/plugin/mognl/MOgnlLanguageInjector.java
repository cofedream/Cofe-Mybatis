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

package tk.cofe.plugin.mognl;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiLanguageInjectionHost;

/**
 * @author : zhengrf
 * @date : 2021-02-19
 */
public final class MOgnlLanguageInjector {
    private final MultiHostRegistrar registrar;
    private final PsiLanguageInjectionHost element;

    public MOgnlLanguageInjector(MultiHostRegistrar registrar, PsiLanguageInjectionHost element) {
        this.registrar = registrar;
        this.element = element;
    }

    public static void injectWithPrefixSuffix(final MultiHostRegistrar registrar,
                                              final PsiLanguageInjectionHost element) {
        new MOgnlLanguageInjector(registrar, element)
                .injectXmlAttributeValue();
    }

    private void injectXmlAttributeValue() {
        final int textLength = element.getTextLength();
        if (textLength < 1) {
            return;
        }
        registrar.startInjecting(MOgnlLanguage.INSTANCE)
                .addPlace("%{", "}", element, new TextRange(1, textLength - 1))
                .doneInjecting();
    }

}
