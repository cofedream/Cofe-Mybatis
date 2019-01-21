package tk.cofedream.plugin.mybatis.dom.mapper.model.attirubte;

import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.Nullable;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface PropertyAttribute extends DomElement {

    @Nullable
    @Attribute("property")
    GenericAttributeValue<String> getProperty();
}
