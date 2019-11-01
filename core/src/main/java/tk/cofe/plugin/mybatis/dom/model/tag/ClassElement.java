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

package tk.cofe.plugin.mybatis.dom.model.tag;

import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.TagValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.convert.ClassElementConverter;
import tk.cofe.plugin.mybatis.dom.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ParameterTypeAttribute;
import tk.cofe.plugin.mybatis.dom.model.dynamic.DynamicSql;

import java.util.Objects;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
public interface ClassElement extends IdAttribute, DynamicSql, ParameterTypeAttribute {

    @NotNull
    @Required
    @Attribute("id")
    @Convert(ClassElementConverter.Id.class)
    GenericAttributeValue<PsiMethod> getId();

    /**
     * 获取Id对应的PsiMethod
     */
    @NotNull
    default Optional<PsiMethod> getIdMethod() {
        return Optional.ofNullable(getId().getValue());
    }

    /**
     * 判断是否为目标Mapper XML文件
     *
     * @param psiMethod 需判断的方法
     * @return true 是目标方法, false 非目标方法
     */
    default boolean isTargetMethod(@Nullable PsiMethod psiMethod) {
        return Objects.equals(getId().getValue(), psiMethod);
    }

    @TagValue
    String getValue();

    @TagValue
    void setValue(String content);

}
