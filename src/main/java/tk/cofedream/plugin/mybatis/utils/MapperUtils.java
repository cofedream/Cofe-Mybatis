package tk.cofedream.plugin.mybatis.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.MyBatisDomConstants;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Delete;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Insert;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Mapper;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Select;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Update;
import tk.cofedream.plugin.mybatis.service.MapperService;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public class MapperUtils {
    public static boolean isElementWithMapperXMLFile(@Nullable PsiElement element) {
        return element instanceof XmlElement && MapperService.isMapperXmlFile(element.getContainingFile());
    }

    /**
     * 基础 增删拆改操作
     * @param element 元素
     * @return 判断是否增删查该操作标签内的元素
     * @see Select
     * @see Update
     * @see Delete
     * @see Insert
     */
    public static boolean isBaseStatementElement(final PsiElement element) {
        if (element == null) {
            return false;
        }
        DomElement domElement = DomUtil.getDomElement(element);
        for (Class<?> clazz : MyBatisDomConstants.BASIC_OPERATION) {
            if (clazz.isInstance(domElement)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @NonNls
    public static Mapper getMapper(@NotNull DomElement element) {
        Optional<Mapper> mapper = Optional.ofNullable(DomUtil.getParentOfType(element, Mapper.class, true));
        if (mapper.isPresent()) {
            return mapper.get();
        } else {
            throw new IllegalArgumentException("Unknown element");
        }
    }
}
