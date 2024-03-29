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

package tk.cofe.plugin.mybatis.service.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.PsiJavaUtils;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.mix.CRUDMix;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.service.MapperService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public class MapperServiceImpl implements MapperService {
    private final Project project;
    private final DomService domService;
    private final JavaPsiService javaService;

    public MapperServiceImpl(Project project) {
        this.project = project;
        this.domService = DomService.getInstance();
        this.javaService = JavaPsiService.getInstance(project);
    }

    @Override
    public boolean isMapperClass(@Nullable final PsiClass mapperClass) {
        if (mapperClass == null) {
            return false;
        }
        return PsiJavaUtils.hasAnnotation(mapperClass, Annotation.MAPPER) // 有 @Mapper 注解
                || getMapperStream().anyMatch(mapper -> mapper.isTargetMapper(mapperClass));
    }

    @Override
    public List<Mapper> findMapperXmls(@Nullable PsiClass mapperClass) {
        return getMapperStream(mapperClass).collect(Collectors.toList());
    }

    @Override
    public List<CRUDMix> findStatemtnts(@Nullable final PsiClass mapperClass) {
        if (mapperClass == null) {
            return Collections.emptyList();
        }
        return findStatementsStream(mapperClass).collect(Collectors.toList());
    }

    @Override
    public Optional<CRUDMix> findStatement(PsiMethod method) {
        return Optional.ofNullable(method)
                .map(PsiMember::getContainingClass)
                .flatMap(psiClass -> findStatementsStream(psiClass)
                        .filter(mix -> mix.isTargetMethod(method))
                        .findFirst());
    }

    @Override
    public ResultMap findResultMap(String resultMap) {
        if (!resultMap.contains(".")) {
            return null;
        }
        int i = resultMap.lastIndexOf(".");
        String pre = resultMap.substring(0, i);
        String suf = resultMap.substring(i + 1);
        return javaService.findPsiClass(pre)
                .map(this::getMapperStream)
                .flatMap(mapperStream -> mapperStream
                        .flatMap(mapper -> mapper.getResultMaps().stream())
                        .filter(item -> item.isEqualsId(suf))
                        .findFirst())
                .orElse(null);
    }

    @Override
    public boolean existStatement(final PsiMethod method) {
        if (method == null) {
            return false;
        }
        if (method.getContainingClass() == null) {
            return false;
        }
        for (Annotation annotation : Annotation.STATEMENT_ANNOTATIONS) {
            if (PsiJavaUtils.hasAnnotation(method, annotation)) {
                return true;
            }
        }
        return findStatementsStream(method.getContainingClass()).anyMatch(mix -> mix.isTargetMethod(method));
    }

    @Override
    @NotNull
    public Stream<Mapper> getMapperStream() {
        return domService.getFileElements(Mapper.class, project, GlobalSearchScope.projectScope(project)).stream()
                .map(DomFileElement::getRootElement);
    }

    @NotNull
    private Stream<CRUDMix> findStatementsStream(@Nullable final PsiClass mapperClass) {
        if (mapperClass == null) {
            return Stream.empty();
        }
        return getMapperStream(mapperClass).flatMap(mapper -> mapper.getCRUDMixs().stream());
    }

    @Override
    @NotNull
    public Stream<Mapper> getMapperStream(@Nullable final PsiClass mapperClass) {
        if (mapperClass == null) {
            return Stream.empty();
        }
        return getMapperStream().filter(mapper -> mapper.isTargetMapper(mapperClass));
    }
}
