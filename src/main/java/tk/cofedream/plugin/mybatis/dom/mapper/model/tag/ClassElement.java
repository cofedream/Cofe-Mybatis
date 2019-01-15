package tk.cofedream.plugin.mybatis.dom.mapper.model.tag;

import com.intellij.util.xml.DomUtil;
import tk.cofedream.plugin.mybatis.dom.mapper.model.attirubte.IdAttribute;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
public interface ClassElement extends IdAttribute {

    default Optional<String> getNamespaceValue() {
        return Optional.ofNullable(DomUtil.getParentOfType(this, Mapper.class, true)).flatMap(Mapper::getNamespaceValue);
    }
}
