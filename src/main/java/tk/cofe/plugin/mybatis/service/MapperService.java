package tk.cofe.plugin.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Mapper;

import java.util.Collection;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public interface MapperService {
    static MapperService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MapperService.class);
    }

    /**
     * 判断 MapperInterface 是否存在与之对应的 Mapper 文件
     * @param mapperClass MapperInterface
     * @return 匹配到的 Mapper Xml 文件
     */
    @NotNull
    Collection<Mapper> findMapperXmls(@NotNull PsiClass mapperClass);

    /**
     * 获取Mapper Dom 文件
     * @return Mapper文件
     */
    @NotNull
    Collection<Mapper> findAllMappers();

    /**
     * 查找 Dom 文件
     * @param clazz Domm描述类 {@code extends DomElement}
     * @param <T>   {@code T extends DomElement}
     * @return 匹配的Dom文件
     */
    <T extends DomElement> Collection<T> findDomElements(@NotNull Class<T> clazz);

}
