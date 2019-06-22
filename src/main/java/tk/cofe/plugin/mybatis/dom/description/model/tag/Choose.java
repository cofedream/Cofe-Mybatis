package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author : zhengrf
 * @date : 2019-01-20
 */
public interface Choose extends DomElement {

    @SubTagList("when")
    List<When> getWhens();

    @SubTagList("otherwise")
    List<Otherwise> getOtherwises();
}
