package tk.cofe.plugin.mybatis.dom;

import org.jetbrains.annotations.NonNls;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.Delete;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.Insert;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.Select;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.Update;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * DOM-related constants.
 * @author : zhengrf
 * @date : 2019-01-02
 */
public final class MyBatisDomConstants {
    public static final List<Class<? extends ClassElement>> BASIC_OPERATION = Collections.unmodifiableList(Arrays.asList(Select.class, Update.class, Insert.class, Delete.class));

    /**
     * DOM-Namespace-Key for mapper.xml
     */
    @NonNls
    public static final String MAPPER_NAMESPACE_KEY = "mapper namespace";
    /**
     * DOM-Namespace-Key for config.xml
     */
    @NonNls
    public static final String CONFIG_NAMESPACE_KEY = "configuration namespace";

    private MyBatisDomConstants() {
    }
}
