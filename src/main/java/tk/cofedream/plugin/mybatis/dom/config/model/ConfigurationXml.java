package tk.cofedream.plugin.mybatis.dom.config.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Namespace;
import tk.cofedream.plugin.mybatis.dom.MyBatisDomConstants;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
@Namespace(MyBatisDomConstants.CONFIG_NAMESPACE_KEY)
public interface ConfigurationXml extends DomElement {
    String TAG_NAME = "configuration";
}
