/*
 * Copyright (C) 2019-2022 cofe
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

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.bundle.MyBatisBundle;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.service.MapperService;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : cofe
 * @date : 2022-01-08
 */
public class ExtendsConverter extends ResolvingConverter<ResultMap> {

    @Override
    public String getErrorMessage(@Nullable final String s, final ConvertContext context) {
        if (StringUtil.isEmpty(s)) {
            return MyBatisBundle.message("error.missing.resultMap.message");
        }
        ResultMap currentResultMap = DomUtils.getParentOfType(context.getInvocationElement(), ResultMap.class);
        if (currentResultMap.isEqualsId(s)) {
            return MyBatisBundle.message("error.recursive.resultMap.message");
        }
        return MyBatisBundle.message("error.cannot.resolve.resultMap.message", s);
    }

    @Override
    public @NotNull Collection<? extends ResultMap> getVariants(ConvertContext context) {
        ResultMap currentResultMap = DomUtils.getParentOfType(context.getInvocationElement(), ResultMap.class);
        return DomUtils.getParentOfType(currentResultMap, Mapper.class).getResultMaps()
                .stream().filter(i -> !i.isEqualsId(currentResultMap.getIdValue("")))
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable ResultMap fromString(@Nullable @NonNls String s, ConvertContext context) {
        if (StringUtil.isEmpty(s)) {
            return null;
        }
        ResultMap currentResultMap = DomUtils.getParentOfType(context.getInvocationElement(), ResultMap.class);
        if (currentResultMap.isEqualsId(s)) {
            return null;
        }
        if (s.contains(".")) {
            // 如果包含.则查找所有文件
            int i = s.lastIndexOf(".");
            String pre = s.substring(0, i);
            String suf = s.substring(i + 1);
            return JavaPsiService.getInstance(context.getProject())
                    .findPsiClass(pre)
                    .map(MapperService.getInstance(context.getProject())::getMapperStream)
                    .flatMap(mapperStream -> mapperStream
                            .flatMap(mapper -> mapper.getResultMaps().stream())
                            .filter(item -> item.isEqualsId(suf))
                            .findFirst())
                    .orElse(null);
        }
        Mapper mapper = DomUtils.getParentOfType(context.getInvocationElement(), Mapper.class);
        for (ResultMap resultMap : mapper.getResultMaps()) {
            if (resultMap.isEqualsId(s)) {
                return resultMap;
            }
        }
        return null;
    }

    @Override
    public @Nullable String toString(@Nullable ResultMap resultMap, ConvertContext context) {
        return Optional.ofNullable(resultMap)
                .flatMap(ResultMap::getIdValue)
                .orElse(null);
    }
}
