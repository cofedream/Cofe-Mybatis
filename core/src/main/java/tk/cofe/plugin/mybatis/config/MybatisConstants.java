package tk.cofe.plugin.mybatis.config;

import org.jetbrains.annotations.NonNls;

/**
 * 常量
 *
 * @author : zhengrf
 * @date : 2020-01-20
 */
public final class MybatisConstants {

    private MybatisConstants() {
    }

    /**
     * DOM-Namespace-Key for config.xml
     */
    @NonNls
    public static final String CONFIG_NAMESPACE_KEY = "configuration namespace";
    @NonNls
    public static final String CONFIG_DTD_ID = "-//mybatis.org//DTD Config 3.0//EN";
    @NonNls
    public static final String CONFIG_DTD_URL = "http://mybatis.org/dtd/mybatis-3-config.dtd";
    /**
     * All config.xml DTD-IDs/URIs.
     */
    @NonNls
    public static final String[] CONFIG_NAME_SPACES = {
            CONFIG_DTD_URL, CONFIG_DTD_ID
    };
    /**
     * DOM-Namespace-Key for mapper.xml
     */
    @NonNls
    public static final String MAPPER_NAMESPACE_KEY = "mapper namespace";

    @NonNls
    public static final String MAPPER_DTD_URL = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";

    @NonNls
    public static final String MAPPER_DTD_ID = "-//mybatis.org//DTD Mapper 3.0//EN";
    /**
     * All mapper.xml DTD-IDs/URIs.
     */
    @NonNls
    public static final String[] MAPPER_NAME_SPACES = {
            MAPPER_DTD_URL, MAPPER_DTD_ID
    };


}
