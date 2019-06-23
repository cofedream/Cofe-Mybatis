package tk.cofe.plugin.mybatis.service.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Mapper;
import tk.cofe.plugin.mybatis.service.MapperService;

import java.util.Collection;
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
    public Collection<Mapper> findAllMappers() {
        return findDomElements(Mapper.class);
    }

    @NotNull
    @Override
    public Collection<Mapper> findMapperXmls(@NotNull PsiClass mapperClass) {
        return findAllMappers().stream().filter(mapperXml -> mapperXml.getNamespaceValue().orElse("").equals(mapperClass.getQualifiedName())).collect(Collectors.toList());
    }

    @Override
    public <T extends DomElement> Collection<T> findDomElements(@NotNull Class<T> clazz) {
        return domService.getFileElements(clazz, project, GlobalSearchScope.projectScope(project)).stream().map(DomFileElement::getRootElement).collect(Collectors.toList());
    }

}
