package tk.cofedream.plugin.mybatis.icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Mybatis的图标
 * @author : zhengrf
 * @date : 2019-01-01
 */
public class MybatisIcons {
    public static final Icon MybatisInterface = load("/icon/mybatisInterface.png");
    public static final Icon NavigateToStatement = load("/icon/navigateToStatement.png");
    public static final Icon NavigateToMethod = load("/icon/navigateToMethod.png");

    private static Icon load(final String path) {
        return IconLoader.getIcon(path, MybatisIcons.class);
    }
}
