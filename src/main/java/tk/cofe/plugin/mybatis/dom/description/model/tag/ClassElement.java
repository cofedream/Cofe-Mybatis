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

package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.TagValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.convert.ClassElementConverter;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
public interface ClassElement extends DynamicSql {

    @Required
    @NameValue
    @NotNull
    @Attribute("id")
    @Convert(ClassElementConverter.Id.class)
    GenericAttributeValue<PsiMethod> getId();

    default Optional<String> getNamespaceValue() {
        return Optional.ofNullable(DomUtils.getParentOfType(this, Mapper.class, true)).flatMap(Mapper::getNamespaceValue);
    }

    /**
     * 获取Id值
     * @return Id 值 如果为Null 则返回 ""
     */
    @NotNull
    default Optional<String> getIdValue() {
        return Optional.ofNullable(getId().getValue()).map(PsiMethod::getName);
    }

    @TagValue
    String getValue();

    @TagValue
    void setValue(String content);

}
