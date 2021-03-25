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

package tk.cofe.plugin.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author : zhengrf
 * @date : 2021-03-24
 */
public interface TypeAliasService {

    static TypeAliasService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, TypeAliasService.class);
    }

    String getAliasTypeCanonicalText(String alias);

    PsiClass getAliasPsiClass(String alias);

    boolean isPsiPrimitiveTypeAlias(String alias);

    List<String> getTypeLookup(String text);

}
