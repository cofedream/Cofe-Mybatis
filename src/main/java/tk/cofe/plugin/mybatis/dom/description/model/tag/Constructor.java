package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface Constructor extends DomElement {

    @SubTagList("idArg")
    List<IdArg> getIdArgs();

    @SubTagList("arg")
    List<Arg> getArgs();

}
