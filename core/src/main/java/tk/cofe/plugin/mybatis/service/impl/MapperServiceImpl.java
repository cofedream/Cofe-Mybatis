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
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.MapperService;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<Mapper> findAllMappers() {
        return findDomElements(Mapper.class);
    }

    @Override
    public boolean isMapperClass(final PsiClass mapperClass) {
        if (mapperClass == null) {
            return false;
        }
        return PsiJavaUtils.hasAnnotation(mapperClass, Annotation.MAPPER) // 有 @Mapper 注解
                || findAllMappers().stream().anyMatch(mapper -> mapper.isTargetMapper(mapperClass));
    }

    @Override
    public List<Mapper> findMapperXmls(PsiClass mapperClass) {
        if (mapperClass == null) {
            return Collections.emptyList();
        }
        return findAllMappers().stream().filter(mapper -> mapper.isTargetMapper(mapperClass)).collect(Collectors.toList());
    }

    @Override
    public List<ClassElement> findStatemtnts(final PsiClass mapperClass) {
        if (mapperClass == null) {
            return Collections.emptyList();
        }
        return findMapperXmls(mapperClass).stream().flatMap(mapper -> mapper.getClassElements().stream()).collect(Collectors.toList());
    }

    @Override
    public <T extends DomElement> List<T> findDomElements(@NotNull Class<T> clazz) {
        return domService.getFileElements(clazz, project, GlobalSearchScope.projectScope(project)).stream().map(DomFileElement::getRootElement).collect(Collectors.toList());
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
        return findStatemtnts(psiClass).stream().filter(classElement -> classElement.isTargetMethod(method)).findFirst();
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
        return findStatemtnts(method.getContainingClass()).stream().anyMatch(classElement -> classElement.isTargetMethod(method));
    }
}
