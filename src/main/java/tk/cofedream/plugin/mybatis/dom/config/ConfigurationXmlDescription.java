package tk.cofedream.plugin.mybatis.dom.config;

import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.constants.MybatisConstants;
import tk.cofedream.plugin.mybatis.dom.MyBatisDomConstants;
import tk.cofedream.plugin.mybatis.dom.config.model.ConfigurationXml;
import tk.cofedream.plugin.mybatis.icons.MybatisIcons;

import javax.swing.*;

/**
 * 标注 mybatis configuration XML 文件
 * @author : zhengrf
 * @date : 2019-01-02
 */
public class ConfigurationXmlDescription extends DomFileDescription<ConfigurationXml> {

    public ConfigurationXmlDescription() {
        super(ConfigurationXml.class, ConfigurationXml.TAG_NAME);
    }

    @Override
    protected void initializeFileDescription() {
        registerNamespacePolicy(MyBatisDomConstants.CONFIG_NAMESPACE_KEY, MybatisConstants.CONFIG_DTDS);
    }

    @Nullable
    @Override
    public Icon getFileIcon(int flags) {
        return MybatisIcons.MybatisInterface;
    }
}
