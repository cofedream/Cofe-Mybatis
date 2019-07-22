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

package tk.cofe.plugin.mybatis;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class MyBatisBundle extends AbstractBundle {
    private static final String PATH_TO_BUNDLE = "MybatisBundle.properties";
    private static final MyBatisBundle ourInstance = new MyBatisBundle();

    protected MyBatisBundle() {
        super(PATH_TO_BUNDLE);
    }

    public static String message(@NotNull String key, @NotNull Object... params) {
        return ourInstance.getMessage(key, params);
    }
}
