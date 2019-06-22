package tk.cofe.plugin.mybatis.constants;

import org.jetbrains.annotations.NonNls;

/**
 * @author : zhengrf
 * @date : 2019-01-02
 */
public final class Mybatis {

    public static final class Mapper{

        @NonNls
        public static final String MAPPER_DTD_URL = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";
        @NonNls
        public static final String MAPPER_DTD_ID = "-//mybatis.org//DTD Mapper 3.0//EN";
        /**
         * All mapper.xml DTD-IDs/URIs.
         */
        @NonNls
        public static final String[] DTDS = {
                MAPPER_DTD_URL, MAPPER_DTD_ID
        };
        /**
         * DOM-Namespace-Key for mapper.xml
         */
        @NonNls
        public static final String NAMESPACE_KEY = "mapper namespace";
    }

    public static final class Config{

        @NonNls
        public static final String DTD_URL = "http://mybatis.org/dtd/mybatis-3-config.dtd";
        @NonNls
        public static final String DTD_ID = "-//mybatis.org//DTD Config 3.0//EN";
        /**
         * All config.xml DTD-IDs/URIs.
         */
        @NonNls
        public static final String[] DTDS = {
                DTD_URL, DTD_ID
        };
        /**
         * DOM-Namespace-Key for config.xml
         */
        @NonNls
        public static final String NAMESPACE_KEY = "configuration namespace";
    }

}
