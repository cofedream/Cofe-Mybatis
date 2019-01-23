package tk.cofedream.plugin.mybatis;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class MyBatisBundle extends AbstractBundle {
    private static final String PATH_TO_BUNDLE = "MybatisBundle.properties";
    private static final MyBatisBundle ourInstance = new MyBatisBundle();

    protected MyBatisBundle() {
        super(PATH_TO_BUNDLE);
    }

    public static String message(@NotNull String key, @NotNull Object... params) {
        return ourInstance.getMessage(key, params);
    }
}
