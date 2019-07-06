package tk.cofe.plugin.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;

import java.util.List;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public interface MapperService {
    static MapperService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MapperService.class);
    }

    /**
     * 判断是否为 Mapper 接口文件
     * @param mapperClass Class文件
     */
    boolean isMapperClass(@NotNull PsiClass mapperClass);

    /**
     * 判断 MapperInterface 是否存在与之对应的 Mapper 文件
     * @param mapperClass MapperInterface
     * @return 匹配到的 Mapper Xml 文件
     */
    @NotNull
    List<Mapper> findMapperXmls(@NotNull PsiClass mapperClass);

    /**
     * 根据PsiClass 获取 Mapper Xml中的所有 Statement
     * @param mapperClass Mapper Interface
     * @return Mapper Statement
     */
    @NotNull
    List<ClassElement> findMapperStatemtnts(@NotNull PsiClass mapperClass);

    /**
     * 获取Mapper Dom 文件
     * @return Mapper文件
     */
    @NotNull
    List<Mapper> findAllMappers();

    /**
     * 查找 Dom 文件
     * @param <T>   {@code T extends DomElement}
     * @param clazz Domm描述类 {@code extends DomElement}
     * @return 匹配的Dom文件
     */
    <T extends DomElement> List<T> findDomElements(@NotNull Class<T> clazz);

    /**
     * 找到 PsiMethod 对应的Mapper Statement
     * @param method 方法
     * @return Statement 集合
     */
    @NotNull
    List<ClassElement> findStatement(@NotNull PsiMethod method);

    /**
     * 查询PsiMethod对应的Mapper Statement是否存在
     * @param method 方法
     * @return true 存在, false 不存在
     */
    boolean existStatement(@NotNull PsiMethod method);

}
