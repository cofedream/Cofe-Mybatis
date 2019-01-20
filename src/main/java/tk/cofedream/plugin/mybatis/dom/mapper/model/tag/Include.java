package tk.cofedream.plugin.mybatis.dom.mapper.model.tag;

import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import tk.cofedream.plugin.mybatis.dom.mapper.converter.RefIdConverter;

/**
 * {@code <include/>} 标签
 * @author : zhengrf
 * @date : 2019-01-20
 */
public interface Include {

    @Attribute("refid")
    @Convert(RefIdConverter.class)
    GenericAttributeValue<XmlTag> getRefId();
}
