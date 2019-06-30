package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.SubTagList;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ResultTypeAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ValueAttribute;

import java.util.List;

/**
 * {@code <case/>} 标签
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface Case extends ValueAttribute, ResultTypeAttribute, ResultMapAttribute {

    @SubTagList("result")
    List<Result> getResults();

}
