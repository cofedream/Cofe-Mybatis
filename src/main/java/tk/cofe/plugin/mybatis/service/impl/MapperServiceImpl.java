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
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.MapperService;

import java.util.Collections;
import java.util.List;
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

    @NotNull
    @Override
    public List<Mapper> findAllMappers() {
        return findDomElements(Mapper.class);
    }

    @Override
    public boolean isMapperClass(@NotNull final PsiClass mapperClass) {
        return findAllMappers().stream().anyMatch(mapper -> mapper.getNamespaceValue().orElse("").equals(mapperClass.getQualifiedName()));
    }

    @NotNull
    @Override
    public List<Mapper> findMapperXmls(@NotNull PsiClass mapperClass) {
        return findAllMappers().stream().filter(mapperXml -> mapperXml.getNamespaceValue().orElse("").equals(mapperClass.getQualifiedName())).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<ClassElement> findMapperStatemtnts(@NotNull final PsiClass mapperClass) {
        return findMapperXmls(mapperClass).stream().flatMap(mapper -> mapper.getClassElements().stream()).collect(Collectors.toList());
    }

    @Override
    public <T extends DomElement> List<T> findDomElements(@NotNull Class<T> clazz) {
        return domService.getFileElements(clazz, project, GlobalSearchScope.projectScope(project)).stream().map(DomFileElement::getRootElement).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<ClassElement> findStatement(@NotNull PsiMethod method) {
        PsiClass psiClass = method.getContainingClass();
        if (psiClass == null) {
            return Collections.emptyList();
        }
        return findMapperStatemtnts(psiClass).stream()
                .filter(classElement -> classElement.getIdValue().map(id -> id.equals(method.getName())).orElse(false))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existStatement(@NotNull final PsiMethod method) {
        if (method.getContainingClass() == null) {
            return false;
        }
        for (Annotation annotation : Annotation.STATEMENT_ANNOTATIONS) {
            if (method.hasAnnotation(annotation.getQualifiedName())) {
                // 存在注解式或者XML
                return true;
            }
        }
        return findMapperStatemtnts(method.getContainingClass()).stream()
                .anyMatch(classElement -> classElement.getIdValue().map(id -> id.equals(method.getName())).orElse(false));
    }
}
