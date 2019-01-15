package tk.cofedream.plugin.mybatis.service.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Mapper;
import tk.cofedream.plugin.mybatis.service.DomService;
import tk.cofedream.plugin.mybatis.service.MapperService;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public class MapperServiceImpl extends MapperService {
    private final Project project;
    private final DomService domService;

    public MapperServiceImpl(Project project) {
        this.project = project;
        this.domService = DomService.getInstance(project);
    }

    @NotNull
    @Override
    public Collection<Mapper> findAllMappers() {
        return domService.findDomElements(Mapper.class);
    }

    @NotNull
    @Override
    public Collection<Mapper> findMapperXmls(@NotNull PsiClass mapperClass) {
        return findAllMappers().stream().filter(mapperXml -> mapperXml.getNamespaceValue().orElse("").equals(mapperClass.getQualifiedName())).collect(Collectors.toList());
    }
}
