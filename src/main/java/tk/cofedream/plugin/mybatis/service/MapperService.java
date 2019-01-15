package tk.cofedream.plugin.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.constants.MybatisConstants;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Mapper;

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
    public static Optional<Mapper> findMapperXml(@NotNull ClassElement element) {
        return Optional.ofNullable(DomUtil.getParentOfType(element, Mapper.class, true));
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
