package tk.cofedream.plugin.mybatis.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.dom.MyBatisDomConstants;
import tk.cofedream.plugin.mybatis.service.MapperService;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public class MapperUtils {
    public static boolean isElementWithMapperXMLFile(@NotNull PsiElement element) {
        return element instanceof XmlElement && MapperService.isMapperXmlFile(element.getContainingFile());
    }

    /**
     * 基础 增删拆改操作
     * @param element 元素
     * @return 判断是否增删查该操作标签内的元素
     * @see tk.cofedream.plugin.mybatis.dom.mapper.model.Select
     * @see tk.cofedream.plugin.mybatis.dom.mapper.model.Update
     * @see tk.cofedream.plugin.mybatis.dom.mapper.model.Delete
     * @see tk.cofedream.plugin.mybatis.dom.mapper.model.Insert
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
}
