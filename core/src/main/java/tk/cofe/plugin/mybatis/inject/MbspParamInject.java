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

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlText;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbsp.MbspLanguage;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MbspParamInject implements MultiHostInjector, DumbAware {

    @Override
    public void getLanguagesToInject(@NotNull final MultiHostRegistrar registrar, @NotNull final PsiElement context) {
        if (!MybatisUtils.isMapperXmlFile(context.getContainingFile())) {
            return;
        }
        if (context.textContains('{') && context.textContains('}')) {
            String text = context.getText();
            int index = 0;
            while (text.indexOf('{', index) != -1 && text.indexOf('}', index) != -1) {
                int lbrace = text.indexOf('{', index);
                int rbrace = text.indexOf('}', lbrace);
                int extra = text.indexOf(',', lbrace);
                registrar.startInjecting(MbspLanguage.INSTANCE);
                if (extra > 0 && extra < rbrace) {
                    registrar.addPlace("", "}", (PsiLanguageInjectionHost) context, new TextRange(lbrace - 1, extra));
                } else {
                    registrar.addPlace("", "", (PsiLanguageInjectionHost) context, new TextRange(lbrace - 1, rbrace + 1));
                }
                registrar.doneInjecting();
                index = rbrace + 1;
            }
        }
    }

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(XmlText.class);
    }
}
