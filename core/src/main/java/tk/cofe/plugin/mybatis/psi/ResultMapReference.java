package tk.cofe.plugin.mybatis.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.GenericAttributeValue;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.util.RecursionUtils;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * ResultMap相关引用
 *
 * @author : zhengrf
 * @date : 2019-11-22
 */
public class ResultMapReference {

    private BiFunction<XmlAttributeValue, PsiElement, ? extends PsiReference> referenceFunction;

    public ResultMapReference(final BiFunction<XmlAttributeValue, PsiElement, ? extends PsiReference> referenceFunction) {
        this.referenceFunction = referenceFunction;
    }

    /**
     * 处理XML中 ResultMap标签
     */
    public Function<Mapper, PsiReference[]> getResultMapFunction(final PsiElement element, final String rawText) {
        return mapper -> processSteam(element, getResultMapStream(rawText, mapper).map(ResultMap::getExtends));
    }

    /**
     * 处理XML中 Select标签
     */
    public Function<Mapper, PsiReference[]> getSelectFunction(final PsiElement element, final String rawText) {
        return mapper -> processSteam(element, mapper.getSelects().stream()
                .filter(select -> select.isTargetResultMapAttribute(rawText))
                .map(ResultMapAttribute::getResultMap));
    }

    /**
     * 处理XML中 ResultMap标签内子标签的ResultMap属性
     */
    public Function<Mapper, PsiReference[]> getResultMapAttributeFunction(final PsiElement element, final String rawText) {
        return mapper -> processSteam(element, getResultMapStream(rawText, mapper)
                .flatMap(resultMap -> RecursionUtils.recursionResultMapAttribute(resultMap).stream())
                .filter(resultMapAttribute -> resultMapAttribute.isTargetResultMapAttribute(rawText))
                .map(ResultMapAttribute::getResultMap));
    }

    private Stream<ResultMap> getResultMapStream(final String rawText, final Mapper mapper) {
        return mapper.getResultMaps().stream().filter(resultMap -> !resultMap.isEqualsId(rawText) && Objects.equals(rawText, resultMap.getExtendsValue().orElse(null)));
    }

    private PsiReference[] processSteam(final PsiElement element, final Stream<GenericAttributeValue<ResultMap>> stream) {
        return stream.map(GenericAttributeValue::getXmlAttributeValue)
                .filter(Objects::nonNull)
                .map(xmlAttributeValue -> referenceFunction.apply(xmlAttributeValue, element)).toArray(PsiReference[]::new);
    }
}
