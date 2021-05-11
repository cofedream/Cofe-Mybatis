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

import com.rits.cloning.Cloner;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhengrf
 * @date : 2021-04-29
 */
public class ApplicationSettings implements Cloneable {

    private List<MapperScan> mapperScanList;

    public ApplicationSettings() {
        this.mapperScanList = new ArrayList<>();
        this.mapperScanList.add(new MapperScan("org.mybatis.spring.annotation.MapperScan"));
    }

    public ApplicationSettings(List<MapperScan> mapperScanList) {
        this.mapperScanList = mapperScanList;
    }

    @NotNull
    public List<MapperScan> getMapperScanList() {
        return mapperScanList;
    }

    public ApplicationSettings setMapperScanList(List<MapperScan> mapperScanList) {
        this.mapperScanList = mapperScanList;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, false);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public ApplicationSettings clone() {
        final Cloner cloner = new Cloner();
        return cloner.deepClone(this);
    }
}
