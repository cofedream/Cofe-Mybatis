package tk.cofedream.plugin.mybatis.dom.psi;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.patterns.XmlNamedElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.patterns.XmlTagPattern;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.dom.psi.providers.ClassElementIdAttributeReferenceProvider;
import tk.cofedream.plugin.mybatis.dom.psi.providers.IncludeTagReferenceProvider;

/**
 * 注册 Xml Attribute 引用<br/>
 * 直接支持重命名
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MapperXmlReferenceContributor extends PsiReferenceContributor {
    private static final XmlTagPattern.Capture CLASS_ELEMENT_PATTERN = XmlPatterns.xmlTag().withLocalName("select", "update", "delete", "insert");
    private static final XmlTagPattern.Capture INCLUDE_PATTERN = XmlPatterns.xmlTag().withLocalName("include");

    private static final XmlNamedElementPattern.XmlAttributePattern ID_ATTRIBUTE_PATTERN = XmlPatterns.xmlAttribute().withLocalName("id");
    //private static final XmlNamedElementPattern.XmlAttributePattern RESULT_TYPE = XmlPatterns.xmlAttribute().withLocalName("resultType");

    //private static final XmlAttributeValuePattern XML_ATTRIBUTE_VALUE_PATTERN = XmlPatterns.xmlAttributeValue();
    private static final XmlAttributeValuePattern CLASS_ELEMENT_ID_PATTERN = XmlPatterns.xmlAttributeValue().withParent(ID_ATTRIBUTE_PATTERN.withParent(CLASS_ELEMENT_PATTERN));
    private static final XmlAttributeValuePattern INCLUDE_ID_PATTERN = XmlPatterns.xmlAttributeValue().withParent(ID_ATTRIBUTE_PATTERN.withParent(INCLUDE_PATTERN));

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        //registrar.registerReferenceProvider(XML_ATTRIBUTE_VALUE_PATTERN, new XmlAttributeValueReferenceProvider());
        registrar.registerReferenceProvider(CLASS_ELEMENT_ID_PATTERN, new ClassElementIdAttributeReferenceProvider());
        registrar.registerReferenceProvider(INCLUDE_ID_PATTERN, new IncludeTagReferenceProvider());
    }

}
