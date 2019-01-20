package tk.cofedream.plugin.mybatis.dom.psi.providers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.ElementPresentationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Mapper;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Sql;
import tk.cofedream.plugin.mybatis.service.MapperService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-17
 */
public class IncludeTagReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return new PsiReference[] {new ResultTypeAttributeReference(element)};
    }

    /**
     * @author : zhengrf
     * @date : 2019-01-05
     */
    private static class ResultTypeAttributeReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
        ResultTypeAttributeReference(@NotNull PsiElement element) {
            super(element);
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean incompleteCode) {
            XmlAttributeValue originalElement = (XmlAttributeValue) myElement.getOriginalElement();
            Optional<Mapper> mapper = MapperService.getMapper(originalElement);
            if (!mapper.isPresent()) {
                return new ResolveResult[0];
            }
            List<Sql> sqls = mapper.get().getSqls();
            List<ResolveResult> result = new ArrayList<>();
            sqls.forEach(sql -> sql.getIdValue().ifPresent(id -> {
                if (id.equals(originalElement.getValue())) {
                    if (sql.getXmlElement() != null) {
                        result.add(new PsiElementResolveResult(sql.getXmlElement()));
                    }
                }
            }));
            return result.toArray(new ResolveResult[0]);
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            ResolveResult[] resolveResults = multiResolve(false);
            return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return MapperService.getMapper(((XmlAttributeValue) myElement.getOriginalElement()))
                    .map(mapper -> ElementPresentationManager.getInstance().createVariants(mapper.getSqls()))
                    .orElseGet(() -> new Object[0]);
        }

    }

}
