package tk.cofe.plugin.mybatis.dom.config.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Namespace;
import tk.cofe.plugin.mybatis.constants.Mybatis;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
@Namespace(Mybatis.Config.NAMESPACE_KEY)
public interface ConfigurationXml extends DomElement {
    String TAG_NAME = "configuration";
}
