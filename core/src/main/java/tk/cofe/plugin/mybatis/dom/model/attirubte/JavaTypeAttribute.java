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

package tk.cofe.plugin.mybatis.dom.model.attirubte;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-06-30
 */
public interface JavaTypeAttribute extends DomElement {

    @Attribute("javaType")
    GenericAttributeValue<PsiClass> getJavaType();

    /**
     * 获取 javaType 值
     *
     * @return javaType 值 如果为Null 则返回 ""
     */
    default Optional<PsiClass> getJavaTypeValue() {
        return Optional.ofNullable(getJavaType().getValue());
    }
}
