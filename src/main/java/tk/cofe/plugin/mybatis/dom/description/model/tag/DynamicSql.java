package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * {@code <sql>} 标签
 * @author : zhengrf
 * @date : 2019-01-20
 */
public interface DynamicSql extends DomElement {

    @SubTagList("include")
    List<Include> getIncludes();

    @SubTagList("trim")
    List<Trim> getTrims();

    @SubTagList("where")
    List<Where> getWheres();

    @SubTagList("set")
    List<Set> getSets();

    @SubTagList("foreach")
    List<Foreach> getForeachs();

    @SubTagList("choose")
    List<Choose> getChooses();

    @SubTagList("if")
    List<If> getIfs();

    @SubTagList("bind")
    List<Bind> getBinds();
}
