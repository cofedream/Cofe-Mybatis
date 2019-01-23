package tk.cofedream.plugin.mybatis.dom.mapper.converter.attribute;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ResultMap;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class PropertyConverter extends ResolvingConverter.StringConverter {

    @NotNull
    @Override
    public Collection<? extends String> getVariants(ConvertContext context) {
        ResultMap resultMap = DomUtil.getParentOfType(context.getInvocationElement(), ResultMap.class, true);
        if (resultMap == null) {
            return Collections.emptyList();
        }
        return resultMap.getTypeValue().map(type -> JavaPsiService.getInstance(context.getProject()).getPsiClass(type).map(psiClass -> {
            Collection<String> result = new ArrayList<>();
            for (PsiField field : psiClass.getAllFields()) {
                if (!field.hasModifierProperty(PsiModifier.FINAL) || !field.hasModifierProperty(PsiModifier.STATIC)) {
                    result.add(field.getName());
                }
            }
            return result;
        }).orElse(Collections.emptyList())).orElse(Collections.emptyList());
    }

    @Nullable
    @Override
    public LookupElement createLookupElement(String property) {
        if (property == null) {
            return null;
        }
        return LookupElementBuilder.create(property);
    }

    @Nullable
    @Override
    public PsiElement resolve(String o, ConvertContext context) {
        ResultMap resultMap = DomUtil.getParentOfType(context.getInvocationElement(), ResultMap.class, true);
        if (resultMap == null) {
            return null;
        }
        return resultMap.getTypeValue().map(type -> JavaPsiService.getInstance(context.getProject()).getPsiClass(type).map(psiClass -> {
            for (PsiField field : psiClass.getAllFields()) {
                if (o.equals(field.getName())) {
                    return field;
                }
            }
            return null;
        }).orElse(null)).orElse(null);
    }
}
