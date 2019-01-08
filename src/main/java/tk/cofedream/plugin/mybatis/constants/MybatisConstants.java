package tk.cofedream.plugin.mybatis.constants;

import org.jetbrains.annotations.NonNls;

/**
 * @author : zhengrf
 * @date : 2019-01-02
 */
public final class MybatisConstants {

    @NonNls
    public static final String MYBATIS_3_MAPPER_DTD_URL = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";
    @NonNls
    public static final String MYBATIS_3_MAPPER_DTD_ID = "-//mybatis.org//DTD Mapper 3.0//EN";
    /**
     * All mapper.xml DTD-IDs/URIs.
     */
    @NonNls
    public static final String[] MAPPER_DTDS = {
            MYBATIS_3_MAPPER_DTD_URL, MYBATIS_3_MAPPER_DTD_ID
    };
    @NonNls
    public static final String MYBATIS_3_CONFIG_DTD_URL = "http://mybatis.org/dtd/mybatis-3-config.dtd";
    @NonNls
    public static final String MYBATIS_3_CONFIG_DTD_ID = "-//mybatis.org//DTD Config 3.0//EN";
    /**
     * All config.xml DTD-IDs/URIs.
     */
    @NonNls
    public static final String[] CONFIG_DTDS = {
            MYBATIS_3_CONFIG_DTD_URL, MYBATIS_3_CONFIG_DTD_ID
    };

    private MybatisConstants() {
    }
}
