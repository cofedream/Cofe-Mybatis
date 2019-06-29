package tk.cofe.plugin.mybatis.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomService;
import com.intellij.util.xml.XmlFileHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.constants.Mybatis;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Delete;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Insert;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Select;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Update;

import java.util.Arrays;
import java.util.Collection;

/**
 * mybatis Psi工具类
 * @author : zhengrf
 * @date : 2019-06-23
 */
public class PsiMybatisUtils {
    @Nullable
    public static Mapper getMapper(@NotNull DomElement element) {
        return DomUtils.getParentOfType(element, Mapper.class, true);
    }

    @Nullable
    public static Mapper getMapper(@NotNull XmlElement element) {
        DomManager domManager = DomManager.getDomManager(element.getProject());
        DomFileElement<Mapper> fileElement = domManager.getFileElement(((XmlFile) element.getContainingFile()), Mapper.class);
        return fileElement == null ? null : fileElement.getRootElement();
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
        return isTargetXml(((XmlFile) file), Mybatis.Mapper.DTDS);
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
    public static boolean isBaseStatementElement(@Nullable final XmlElement xmlElement) {
        if (xmlElement == null) {
            return false;
        }
        // todo 下面两个判断可以合并
        DomElement domElement = DomUtils.getDomElement(xmlElement);
        if (!(domElement instanceof ClassElement)) {
            return false;
        }
        return Mapper.BASIC_OPERATION.stream().anyMatch(clazz -> clazz.isInstance(domElement));
    }

    public static boolean isTargetXml(@NotNull XmlFile xmlFile, @Nullable String... namespaces) {
        if (namespaces == null || namespaces.length == 0) {
            return false;
        }
        Collection<String> namespacesSet = Arrays.asList(namespaces);
        XmlFileHeader header = DomService.getInstance().getXmlFileHeader(xmlFile);
        return namespacesSet.contains(header.getPublicId()) || namespacesSet.contains(header.getSystemId()) || namespacesSet.contains(header.getRootTagNamespace());
    }

    @Nullable
    public static DomElement getDomElement(@NotNull XmlElement xmlElement) {
        if (xmlElement instanceof XmlTag) {
            return DomManager.getDomManager(xmlElement.getProject()).getDomElement(((XmlTag) xmlElement));
        } else if (xmlElement instanceof XmlAttribute) {
            return DomManager.getDomManager(xmlElement.getProject()).getDomElement(((XmlAttribute) xmlElement));
        }
        return null;
    }
}
