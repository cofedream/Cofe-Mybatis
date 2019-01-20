package tk.cofedream.plugin.mybatis.dom.mapper.model.tag;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@code <sql>} 标签
 * @author : zhengrf
 * @date : 2019-01-20
 */
public interface DynamicSql extends DomElement {
    @NotNull
    @SubTagList("include")
    List<Include> getIncludes();

    @NotNull
    @SubTagList("trim")
    List<Trim> getTrims();

    @NotNull
    @SubTagList("where")
    List<Where> getWheres();

    @NotNull
    @SubTagList("set")
    List<Set> getSets();

    @NotNull
    @SubTagList("foreach")
    List<Foreach> getForeachs();

    @NotNull
    @SubTagList("choose")
    List<Choose> getChooses();

    @NotNull
    @SubTagList("if")
    List<If> getIfs();
}
