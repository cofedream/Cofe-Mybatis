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

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.DomJavaUtil;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.util.TypeAliasUtils;

/**
 * @author : zhengrf
 * @date : 2019-09-20
 */
public class ResultTypeConverter extends Converter<PsiClass> {

    @Override
    public PsiClass fromString(final String s, final ConvertContext context) {
        if (StringUtil.isEmptyOrSpaces(s)) {
            return null;
        }
        String str = s;
        if (StringUtil.isNotEmpty(s)) {
            String typeName = TypeAliasUtils.getTypeName(s);
            if (typeName != null) {
                str = typeName;
            }
        }
        return DomJavaUtil.findClass(str.trim(), context.getInvocationElement());
    }

    @Nullable
    @Override
    public String toString(@Nullable final PsiClass psiClass, final ConvertContext context) {
        return psiClass == null ? null : psiClass.getQualifiedName();
    }

}
