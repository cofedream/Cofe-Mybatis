package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.SubTagList;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.JavaTypeAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.JdbcTypeAttribute;

import java.util.List;

/**
 * {@code <discriminator/>} 标签
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface Discriminator extends JavaTypeAttribute,JdbcTypeAttribute {

    @SubTagList("case")
    List<Case> getCases();

}
