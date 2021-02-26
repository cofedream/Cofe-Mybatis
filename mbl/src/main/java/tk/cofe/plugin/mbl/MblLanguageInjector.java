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

package tk.cofe.plugin.mbl;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiLanguageInjectionHost;

/**
 * @author : zhengrf
 * @date : 2021-02-19
 */
public final class MblLanguageInjector {
    private final MultiHostRegistrar registrar;
    private final PsiLanguageInjectionHost element;

    public MblLanguageInjector(MultiHostRegistrar registrar, PsiLanguageInjectionHost element) {
        this.registrar = registrar;
        this.element = element;
    }

    public static void injectWithPrefixSuffix(final MultiHostRegistrar registrar,
                                              final PsiLanguageInjectionHost element) {
        new MblLanguageInjector(registrar, element)
                .injectXmlAttributeValue();
    }

    public static void injectOccurrences(final MultiHostRegistrar registrar,
                                         final PsiLanguageInjectionHost element) {
        new MblLanguageInjector(registrar, element).injectOccurrences();
    }

    private void injectXmlAttributeValue() {
        final int textLength = element.getTextLength();
        if (textLength < 1) {
            return;
        }
        registrar.startInjecting(MblLanguage.INSTANCE)
                .addPlace("#{", "}", element, new TextRange(1, textLength - 1))
                .doneInjecting();
    }

    private void injectOccurrences() {
        final int textLength = element.getTextLength();
        if (textLength < 1) {
            return;
        }
        if (!(element.textContains('{') && element.textContains('}'))) {
            return;
        }
        String text = element.getText();
        int index = 0;
        while (text.indexOf('{', index) != -1 && text.indexOf('}', index) != -1) {
            int lbrace = text.indexOf('{', index);
            int rbrace = text.indexOf('}', lbrace);
            registrar.startInjecting(MblLanguage.INSTANCE)
                    .addPlace(null, null, element, new TextRange(lbrace - 1, rbrace + 1))
                    .doneInjecting();
            index = rbrace + 1;
        }
    }
}
