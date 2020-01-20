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

package tk.cofe.plugin.mybatis.config;

import com.intellij.javaee.ResourceRegistrar;
import com.intellij.javaee.StandardResourceProvider;
import org.jetbrains.annotations.NonNls;

/**
 * @author : zhengrf
 * @date : 2020-01-20
 */
public class MybatisResourceProvider implements StandardResourceProvider {
    private static final String DTD_PATH = "/dtds/";

    @Override
    public void registerResources(final ResourceRegistrar registrar) {
        addDTDResource(MybatisConstants.MAPPER_DTD_ID,
                MybatisConstants.MAPPER_DTD_URL,
                "mybatis-3-mapper.dtd", registrar);
        addDTDResource(MybatisConstants.CONFIG_DTD_ID,
                MybatisConstants.CONFIG_DTD_URL,
                "mybatis-3-config.dtd", registrar);

    }

    private static void addDTDResource(@NonNls final String uri,
                                       @NonNls final String id,
                                       @NonNls final String localFile,
                                       final ResourceRegistrar registrar) {
        registrar.addStdResource(uri, DTD_PATH + localFile, MybatisResourceProvider.class);
        registrar.addStdResource(id, DTD_PATH + localFile, MybatisResourceProvider.class);
    }
}
