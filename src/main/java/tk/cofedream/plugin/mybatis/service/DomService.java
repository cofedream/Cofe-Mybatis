package tk.cofedream.plugin.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public interface DomService {
    static DomService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, DomService.class);
    }

    boolean isTargetXml(@NotNull XmlFile xmlFile, String... namespaces);

    /**
     * 查找 Dom 文件
     * @param clazz Domm描述类 {@code extends DomElement}
     * @param <T>   {@code T extends DomElement}
     * @return 匹配的Dom文件
     */
    <T extends DomElement> Collection<T> findDomElements(@NotNull Class<T> clazz);

    /**
     * 根据Xml元素获取Dom元素
     * @param xmlElement xml元素
     * @return 对应的Dom元素
     */
    Optional<DomElement> getDomElement(@NotNull XmlElement xmlElement);
}
