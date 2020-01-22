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

package tk.cofe.plugin.mybatis.service.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.MapperService;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;

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

    public MapperServiceImpl(Project project) {
        this.project = project;
        this.domService = DomService.getInstance();
    }

    @Override
    public boolean isMapperClass(final PsiClass mapperClass) {
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
    public List<ClassElement> findStatemtnts(@Nullable final PsiClass mapperClass) {
        if (mapperClass == null) {
            return Collections.emptyList();
        }
        return findStatementsStream(mapperClass).collect(Collectors.toList());
    }

    @Override
    public Optional<ClassElement> findStatement(PsiMethod method) {
        if (method == null) {
            return Optional.empty();
        }
        PsiClass psiClass = method.getContainingClass();
        if (psiClass == null) {
            return Optional.empty();
        }
        return findStatementsStream(psiClass).filter(classElement -> classElement.isTargetMethod(method)).findFirst();
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
        return findStatementsStream(method.getContainingClass()).anyMatch(classElement -> classElement.isTargetMethod(method));
    }

    @Override
    @NotNull
    public Stream<Mapper> getMapperStream() {
        return domService.getFileElements(Mapper.class, project, GlobalSearchScope.projectScope(project)).stream()
                .map(DomFileElement::getRootElement);
    }

    @NotNull
    private Stream<ClassElement> findStatementsStream(@Nullable final PsiClass mapperClass) {
        if (mapperClass == null) {
            return Stream.empty();
        }
        return getMapperStream(mapperClass).flatMap(mapper -> mapper.getClassElements().stream());
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
