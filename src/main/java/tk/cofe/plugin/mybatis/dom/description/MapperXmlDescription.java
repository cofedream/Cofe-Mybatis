package tk.cofe.plugin.mybatis.dom.description;

import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.constants.Mybatis;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.icons.MybatisIcons;

import javax.swing.*;

/**
 * 标注 mybatis mapper XML 文件,并提供值参考
 * @author : zhengrf
 * @date : 2019-01-02
 */
public class MapperXmlDescription extends DomFileDescription<Mapper> {

    public MapperXmlDescription() {
        super(Mapper.class, Mapper.TAG_NAME);
    }

    @Override
    protected void initializeFileDescription() {
        registerNamespacePolicy(Mybatis.Mapper.NAMESPACE_KEY, Mybatis.Mapper.DTDS);
    }

    @Nullable
    @Override
    public Icon getFileIcon(int flags) {
        return MybatisIcons.MybatisInterface;
    }
}
