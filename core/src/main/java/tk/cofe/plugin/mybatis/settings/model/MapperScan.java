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

package tk.cofe.plugin.mybatis.settings.model;

/**
 * @author : zhengrf
 * @date : 2021-04-30
 */
public class MapperScan extends BaseObject{
    private String canonicalName;

    public MapperScan() {
    }

    public MapperScan(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapperScan)) {
            return false;
        }

        MapperScan that = (MapperScan) o;

        return getCanonicalName() != null ? getCanonicalName().equals(that.getCanonicalName()) : that.getCanonicalName() == null;
    }

    @Override
    public int hashCode() {
        return getCanonicalName() != null ? getCanonicalName().hashCode() : 0;
    }
}
