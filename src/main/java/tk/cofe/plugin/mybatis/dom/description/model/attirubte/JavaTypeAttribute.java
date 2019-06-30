package tk.cofe.plugin.mybatis.dom.description.model.attirubte;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.Nullable;

/**
 * @author : zhengrf
 * @date : 2019-06-30
 */
public interface JavaTypeAttribute extends DomElement {

    @Nullable
    @Attribute("javaType")
    GenericAttributeValue<String> getJavaType();
}
