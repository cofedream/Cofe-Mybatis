package tk.cofe.plugin.mybatis.dom.mapper;

import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.constants.Mybatis;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.Mapper;
import tk.cofe.plugin.mybatis.icons.MybatisIcons;
import tk.cofe.plugin.mybatis.dom.MyBatisDomConstants;

import javax.swing.*;

/**
 * 标注 mybatis mapper XML 文件
 * @author : zhengrf
 * @date : 2019-01-02
 */
public class MapperXmlDescription extends DomFileDescription<Mapper> {

    public MapperXmlDescription() {
        super(Mapper.class, Mapper.TAG_NAME);
    }

    @Override
    protected void initializeFileDescription() {
        registerNamespacePolicy(MyBatisDomConstants.MAPPER_NAMESPACE_KEY, Mybatis.MAPPER_DTDS);
    }

    @Nullable
    @Override
    public Icon getFileIcon(int flags) {
        return MybatisIcons.MybatisInterface;
    }
}
