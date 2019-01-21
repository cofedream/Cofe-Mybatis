package tk.cofedream.plugin.mybatis.service.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.XmlFileHeader;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.service.DomService;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public class DomServiceImpl implements DomService {
    private final Project project;
    private final com.intellij.util.xml.DomService domService;

    public DomServiceImpl(Project project) {
        this.project = project;
        this.domService = com.intellij.util.xml.DomService.getInstance();
    }

    @Override
    public <T extends DomElement> Collection<T> findDomElements(@NotNull Class<T> clazz) {
        return domService.getFileElements(clazz, project, GlobalSearchScope.projectScope(project)).stream().map(DomFileElement::getRootElement).collect(Collectors.toList());
    }

    @Override
    public Optional<DomElement> getDomElement(@NotNull XmlElement xmlElement) {
        if (xmlElement instanceof XmlTag) {
            return Optional.ofNullable(DomManager.getDomManager(xmlElement.getProject()).getDomElement(((XmlTag) xmlElement)));
        } else if (xmlElement instanceof XmlAttribute) {
            return Optional.ofNullable(DomManager.getDomManager(xmlElement.getProject()).getDomElement(((XmlAttribute) xmlElement)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean isTargetXml(@NotNull XmlFile xmlFile, String... namespaces) {
        if (namespaces == null || namespaces.length == 0) {
            return false;
        }
        Set<String> namespacesSet = new HashSet<>(Arrays.asList(namespaces));
        XmlFileHeader header = domService.getXmlFileHeader(xmlFile);
        return namespacesSet.contains(header.getPublicId()) || namespacesSet.contains(header.getSystemId()) || namespacesSet.contains(header.getRootTagNamespace());
    }

}
