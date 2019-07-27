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

package tk.cofe.plugin.mybatis.util;

import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.enums.AttributeEnums;

import java.util.Optional;

/**
 * 枚举工具类
 * @author : zhengrf
 * @date : 2019-01-17
 */
public final class EnumUtils {

    /**
     * 解析标签
     * @param targetEnums  目标属性枚举
     * @param xmlAttribute 需要解析的属性
     * @param <T>          目标属性
     * @return 解析结果
     */
    public static <T extends AttributeEnums> Optional<T> parse(@NotNull T[] targetEnums, @NotNull XmlAttribute xmlAttribute) {
        for (T attribute : targetEnums) {
            if (attribute.getValue().equals(xmlAttribute.getName())) {
                return Optional.of(attribute);
            }
        }
        return Optional.empty();
    }
}
