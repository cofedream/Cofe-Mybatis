package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.SubTag;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.KeyPropertyAttribute;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
public interface Insert extends KeyPropertyAttribute, ClassElement {

    @SubTag("selectKey")
    SelectKey getSelectKey();

}
