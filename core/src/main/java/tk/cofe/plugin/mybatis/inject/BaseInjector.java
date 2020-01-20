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

package tk.cofe.plugin.mybatis.inject;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author : zhengrf
 * @date : 2020-01-20
 */
public abstract class BaseInjector implements MultiHostInjector {

    @Override
    public void getLanguagesToInject(@NotNull final MultiHostRegistrar registrar, @NotNull final PsiElement context) {
        if (!MybatisUtils.isMapperXmlFile(context.getContainingFile())) {
            return;
        }
        inject(registrar, context);
    }

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(targetElement());
    }

    abstract void inject(@NotNull final MultiHostRegistrar registrar, @NotNull final PsiElement context);

    abstract Class<? extends PsiElement> targetElement();
}
