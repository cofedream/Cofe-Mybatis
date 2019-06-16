package tk.cofe.plugin.mybatis.dom.psi;

import com.intellij.patterns.XmlNamedElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.patterns.XmlTagPattern;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.psi.providers.ClassElementReferenceProvider;

/**
 * 注册 Xml Attribute 引用<br/>
 * 直接支持重命名
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MapperReferenceContributor extends PsiReferenceContributor {
    /**
     * "select", "update", "delete", "insert" 标签
     */
    private static final XmlTagPattern.Capture CLASS_ELEMENT_PATTERN = XmlPatterns.xmlTag().withLocalName("select", "update", "delete", "insert");

    private static final XmlNamedElementPattern.XmlAttributePattern ID_ATTRIBUTE = XmlPatterns.xmlAttribute("id");
    /**
     * "resultMap" 标签
     */
    private static final XmlTagPattern.Capture RESULTMAP = XmlPatterns.xmlTag().withLocalName("resultMap");
    //private static final XmlNamedElementPattern.XmlAttributePattern RESULT_TYPE = XmlPatterns.xmlAttribute().withLocalName("resultType");

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        //registrar.registerReferenceProvider(XML_ATTRIBUTE_VALUE_PATTERN, new XmlAttributeValueReferenceProvider());
        registrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue()
                        .withParent(ID_ATTRIBUTE
                                .withParent(CLASS_ELEMENT_PATTERN))
                , new ClassElementReferenceProvider.Id());
        // <include refid=""></include> 标签匹配
        //registrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue()
        //        .withParent(XmlPatterns.xmlAttribute("refid")
        //                .withParent(XmlPatterns.xmlTag().withLocalName("include"))), new IncludeTagReferenceProvider());
        // <result extends=""> 匹配
        //registrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue()
        //        .withParent(XmlPatterns.xmlAttribute("extends")
        //                .withParent(RESULTMAP)), new ResultMapReferenceProvider.Extends());
    }

}
