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

package tk.cofe.plugin.mbsp.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import tk.cofe.plugin.mbsp.MbspFile;
import tk.cofe.plugin.mbsp.MbspFileType;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MbspElementFactory {
    public static MbspExpression createProperty(Project project, String name) {
        final MbspFile file = createFile(project, name);
        return (MbspExpression) file.getFirstChild();
    }

    public static MbspFile createFile(Project project, String text) {
        String name = "dummy.mbsp";
        return (MbspFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, MbspFileType.INSTANCE, text);
    }
}
