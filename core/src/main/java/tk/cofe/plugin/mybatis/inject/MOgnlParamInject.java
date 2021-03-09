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

package tk.cofe.plugin.mybatis.inject;

import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlText;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbel.MbELLanguageInjector;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MOgnlParamInject extends BaseInjector implements DumbAware {
    public MOgnlParamInject() {
        super(XmlText.class);
    }

    @Override
    void inject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        MbELLanguageInjector.inject(registrar, (PsiLanguageInjectionHost) context);
    }

}
