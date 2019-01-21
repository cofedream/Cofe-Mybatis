package tk.cofedream.plugin.mybatis.dom.mapper.model.tag;

import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.converter.IncludeConverter;

/**
 * {@code <include/>} 标签
 * @author : zhengrf
 * @date : 2019-01-20
 */
public interface Include extends DomElement {

    @Nullable
    @Attribute("refid")
    @Convert(IncludeConverter.class)
    GenericAttributeValue<XmlTag> getRefId();
}
