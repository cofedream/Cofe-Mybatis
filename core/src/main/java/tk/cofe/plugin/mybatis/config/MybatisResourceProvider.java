package tk.cofe.plugin.mybatis.config;

import com.intellij.javaee.ResourceRegistrar;
import com.intellij.javaee.StandardResourceProvider;
import org.jetbrains.annotations.NonNls;

/**
 * @author : zhengrf
 * @date : 2020-01-20
 */
public class MybatisResourceProvider implements StandardResourceProvider {
    private static final String DTD_PATH = "/dtds/";

    @Override
    public void registerResources(final ResourceRegistrar registrar) {
        addDTDResource(MybatisConstants.MAPPER_DTD_ID,
                MybatisConstants.MAPPER_DTD_URL,
                "mybatis-3-mapper.dtd", registrar);
        addDTDResource(MybatisConstants.CONFIG_DTD_ID,
                MybatisConstants.CONFIG_DTD_URL,
                "mybatis-3-config.dtd", registrar);

    }

    private static void addDTDResource(@NonNls final String uri,
                                       @NonNls final String id,
                                       @NonNls final String localFile,
                                       final ResourceRegistrar registrar) {
        registrar.addStdResource(uri, DTD_PATH + localFile, MybatisResourceProvider.class);
        registrar.addStdResource(id, DTD_PATH + localFile, MybatisResourceProvider.class);
    }
}
