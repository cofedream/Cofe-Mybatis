package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.SubTagList;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.JavaTypeAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.JdbcTypeAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.SelectAttribute;

import java.util.List;

/**
 * {@code <association></association>}
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface Association extends PropertyAttribute, JavaTypeAttribute, JdbcTypeAttribute, SelectAttribute, ResultMapAttribute {

    @SubTagList("id")
    List<Id> getIds();

    @SubTagList("result")
    List<Result> getResult();

}
