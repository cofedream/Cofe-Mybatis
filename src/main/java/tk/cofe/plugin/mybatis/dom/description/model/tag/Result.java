package tk.cofe.plugin.mybatis.dom.description.model.tag;

import tk.cofe.plugin.mybatis.dom.description.model.attirubte.JavaTypeAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.JdbcTypeAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.TypeHandlerAttribute;

/**
 * {@code <result property="" column="" jdbcType=""/>}
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface Result extends PropertyAttribute, JavaTypeAttribute, JdbcTypeAttribute, TypeHandlerAttribute {
}
