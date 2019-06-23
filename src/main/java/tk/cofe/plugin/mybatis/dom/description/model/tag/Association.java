package tk.cofe.plugin.mybatis.dom.description.model.tag;

import tk.cofe.plugin.mybatis.dom.description.model.attirubte.JdbcTypeAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.SelectAttribute;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface Association extends PropertyAttribute, JdbcTypeAttribute, SelectAttribute, ResultMapAttribute {
}