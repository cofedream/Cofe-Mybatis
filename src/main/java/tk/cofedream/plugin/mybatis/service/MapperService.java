package tk.cofedream.plugin.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.constants.MybatisConstants;
import tk.cofedream.plugin.mybatis.dom.MyBatisDomConstants;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Delete;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Insert;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Mapper;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Select;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Update;

import java.util.Collection;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public abstract class MapperService {
    public static MapperService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MapperService.class);
    }

    @NotNull
    public static Optional<Mapper> getMapper(@NotNull DomElement element) {
        return Optional.ofNullable(DomUtil.getParentOfType(element, Mapper.class, true));
    }

    @NotNull
    public static Optional<Mapper> getMapper(@NotNull XmlElement element) {
        DomManager domManager = DomManager.getDomManager(element.getProject());
        DomFileElement<Mapper> fileElement = domManager.getFileElement(((XmlFile) element.getContainingFile()), Mapper.class);
        if (fileElement == null) {
            return Optional.empty();
        }
        return Optional.of(fileElement.getRootElement());
    }

    /**
     * 判断是否为mapper.xml文件
     * @param file 文件
     * @return 结果
     */
    public static boolean isMapperXmlFile(@Nullable PsiFile file) {
        if (!(file instanceof XmlFile)) {
            return false;
        }
        return DomService.getInstance(file.getProject()).isTargetXml(((XmlFile) file), MybatisConstants.MAPPER_DTDS);
    }

    public static boolean isElementWithMapperXMLFile(@Nullable PsiElement element) {
        return element instanceof XmlElement && isMapperXmlFile(element.getContainingFile());
    }

    /**
     * 基础 增删拆改操作
     * @param xmlElement 元素
     * @return 判断是否增删查该操作标签内的元素
     * @see Select
     * @see Update
     * @see Delete
     * @see Insert
     */
    public static boolean isBaseStatementElement(final XmlElement xmlElement) {
        if (xmlElement == null) {
            return false;
        }
        // todo 下面两个判断可以合并
        DomElement domElement = DomUtil.getDomElement(xmlElement);
        if (!(domElement instanceof ClassElement)) {
            return false;
        }
        for (Class<?> clazz : MyBatisDomConstants.BASIC_OPERATION) {
            if (clazz.isInstance(domElement)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断 MapperInterface 是否存在与之对应的 Mapper 文件
     * @param mapperClass MapperInterface
     * @return 匹配到的 Mapper Xml 文件
     */
    @NotNull
    public abstract Collection<Mapper> findMapperXmls(@NotNull PsiClass mapperClass);

    /**
     * 获取Mapper Dom 文件
     * @return Mapper文件
     */
    @NotNull
    public abstract Collection<Mapper> findAllMappers();
}
