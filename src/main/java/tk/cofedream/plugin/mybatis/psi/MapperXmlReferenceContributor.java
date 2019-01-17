package tk.cofedream.plugin.mybatis.psi;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.psi.providers.ClassElementReferenceProvider;

/**
 * 注册 Xml Attribute 引用
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MapperXmlReferenceContributor extends PsiReferenceContributor {
    private static final XmlAttributeValuePattern XML_ATTRIBUTE_VALUE_PATTERN = XmlPatterns.xmlAttributeValue();
    private static final XmlAttributeValuePattern CLASS_ELEMENT_PATTERN = XmlPatterns.xmlAttributeValue()
            .withParent(XmlPatterns.xmlAttribute().withLocalName("id")
                    .withParent(XmlPatterns.xmlTag().withName("select", "update", "delete", "insert")));

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        //registrar.registerReferenceProvider(XML_ATTRIBUTE_VALUE_PATTERN, new XmlAttributeValueReferenceProvider());
        registrar.registerReferenceProvider(CLASS_ELEMENT_PATTERN, new ClassElementReferenceProvider());
    }

}
