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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.utils.DomUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-15
 */
public interface IdAttribute extends DomElement {

    @NotNull
    GenericAttributeValue<?> getId();

    /**
     * 获取Id值
     *
     * @return Id 值 如果为Null 则返回 ""
     */
    @NotNull
    default Optional<String> getIdValue() {
        return DomUtils.getAttributeValueOpt(getId());
    }

    /**
     * 获取Id值
     *
     * @param defaultValue 默认ID值
     * @return Id 值
     */
    @Nullable
    default String getIdValue(final String defaultValue) {
        return getIdValue().orElse(defaultValue);
    }

    /**
     * 判断ID是否相同
     *
     * @param id id值
     * @return true 相等,false 不相等
     */
    default boolean isEqualsId(final String id) {
        return StringUtil.isNotEmpty(id) && Objects.equals(id, getIdValue(null));
    }
}
