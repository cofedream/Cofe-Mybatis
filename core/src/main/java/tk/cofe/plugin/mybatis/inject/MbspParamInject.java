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
        if ((context.textContains('#') || context.textContains('$'))
                && context.textContains('{')
                && context.textContains('}')) {
            String text = context.getText();
            int index = 0;
            int start;
            int lbrace;
            int rbrace;
            while (((start = text.indexOf('#', index)) != -1
                    || (start = text.indexOf('$', index)) != -1) // 判断开头节点
                    // 开始节点
                    && (lbrace = text.indexOf('{', start)) != -1
                    && (rbrace = text.indexOf('}', lbrace)) != -1) {
                // 设置分隔节点
                int split = text.indexOf(",", lbrace);
                // 结束节点
                int end = split > 0 && split < rbrace ? split : rbrace;
                registrar.startInjecting(MbspLanguage.INSTANCE)
                        .addPlace("", "}", (PsiLanguageInjectionHost) context, new TextRange(lbrace - 1, end))
                        .doneInjecting();
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
