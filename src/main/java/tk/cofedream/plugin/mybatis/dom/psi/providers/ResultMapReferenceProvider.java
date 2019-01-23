package tk.cofedream.plugin.mybatis.dom.psi.providers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.ElementPresentationManager;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.dom.mapper.converter.ResultMapConverter;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Mapper;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ResultMap;
import tk.cofedream.plugin.mybatis.service.MapperService;
import tk.cofedream.plugin.mybatis.utils.EmptyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ResultMap Reference
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class ResultMapReferenceProvider {
    /**
     * Extends 属性引用
     * @see ResultMapConverter
     */
    public static class Extends extends PsiReferenceProvider {

        @NotNull
        @Override
        public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            return new PsiReference[] {new Reference(element)};
        }

        public static class Reference extends PsiReferenceBase.Poly<PsiElement> {

            public Reference(@NotNull PsiElement element) {
                super(element);
            }

            @NotNull
            @Override
            public ResolveResult[] multiResolve(boolean incompleteCode) {
                ResultMap domElement = (ResultMap) DomUtil.getDomElement(PsiTreeUtil.getParentOfType(myElement, XmlTag.class));
                if (domElement == null) {
                    return EmptyUtil.EMPTY_RESOLVE_RESULTS;
                }
                Optional<String> extendsValue = domElement.getExtendsValue();
                if (!extendsValue.isPresent()) {
                    return EmptyUtil.EMPTY_RESOLVE_RESULTS;
                }
                Optional<Mapper> mapper = MapperService.getMapper((XmlAttributeValue) myElement);
                if (!mapper.isPresent()) {
                    return EmptyUtil.EMPTY_RESOLVE_RESULTS;
                }
                List<ResolveResult> result = new ArrayList<>();
                mapper.get().getResultMaps().forEach(resultMap -> resultMap.getIdValue().ifPresent(id -> {
                    if (id.equals(extendsValue.get())) {
                        result.add(new PsiElementResolveResult(resultMap.getId().getXmlAttributeValue()));
                    }
                }));
                return result.toArray(EmptyUtil.EMPTY_RESOLVE_RESULTS);
            }

            @NotNull
            @Override
            public Object[] getVariants() {
                ResultMap domElement = (ResultMap) DomUtil.getDomElement(PsiTreeUtil.getParentOfType(myElement, XmlTag.class));
                if (domElement == null) {
                    return new Object[0];
                }
                return MapperService.getMapper((XmlAttributeValue) myElement)
                        .map(mapper -> ElementPresentationManager.getInstance().createVariants(mapper.getResultMaps()
                                .stream().filter(resultMap -> resultMap.getIdValue()
                                        .map(otherId -> domElement.getIdValue()
                                                .map(selfId -> !otherId.equals(selfId))
                                                .orElse(false))
                                        .orElse(false))
                                .collect(Collectors.toList())))
                        .orElseGet(() -> new Object[0]);
            }
        }
    }

    /**
     * Extends 属性引用
     */
    public static class Id extends PsiReferenceProvider {

        @NotNull
        @Override
        public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            return new PsiReference[] {new Reference(element)};
        }

        public static class Reference extends PsiReferenceBase.Poly<PsiElement> {

            public Reference(@NotNull PsiElement element) {
                super(element);
            }

            @NotNull
            @Override
            public ResolveResult[] multiResolve(boolean incompleteCode) {
                ResultMap domElement = (ResultMap) DomUtil.getDomElement(PsiTreeUtil.getParentOfType(myElement, XmlTag.class));
                if (domElement == null) {
                    return EmptyUtil.EMPTY_RESOLVE_RESULTS;
                }
                Optional<String> idValue = domElement.getIdValue();
                if (!idValue.isPresent()) {
                    return EmptyUtil.EMPTY_RESOLVE_RESULTS;
                }
                List<ResolveResult> result = new ArrayList<>();
                MapperService.getMapper((XmlAttributeValue) myElement).ifPresent(mapper -> {
                    mapper.getResultMaps().forEach(resultMap -> {
                        resultMap.getExtendsValue().ifPresent(extendsValue -> {
                            if (extendsValue.equals(idValue.get())) {
                                result.add(new PsiElementResolveResult(resultMap.getXmlElement()));

                            }
                        });
                    });
                });
                return result.toArray(EmptyUtil.EMPTY_RESOLVE_RESULTS);
            }

        }
    }
}
