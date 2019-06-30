package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifier;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class PropertyConverter extends ResolvingConverter.StringConverter {

    private static final RowIcon PRIVATE_FIELD_ICON = new RowIcon(PlatformIcons.FIELD_ICON, PlatformIcons.PRIVATE_ICON);

    @NotNull
    @Override
    public Collection<? extends String> getVariants(ConvertContext context) {
        ResultMap resultMap = DomUtils.getParentOfType(context.getInvocationElement(), ResultMap.class, true);
        if (resultMap == null) {
            return Collections.emptyList();
        }
        return resultMap.getTypeValue().map(type -> JavaPsiService.getInstance(context.getProject()).findPsiClass(type)
                .map(psiClass -> Arrays.stream(psiClass.getAllFields())
                        .filter(field -> !field.hasModifierProperty(PsiModifier.FINAL) || !field.hasModifierProperty(PsiModifier.STATIC))
                        .map(NavigationItem::getName)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList()))
                .orElse(Collections.emptyList());
    }

    @Nullable
    @Override
    public LookupElement createLookupElement(String property) {
        return property == null ? null : LookupElementBuilder.create(property).withIcon(PRIVATE_FIELD_ICON);
    }

    @Nullable
    @Override
    public PsiElement resolve(String o, ConvertContext context) {
        ResultMap resultMap = DomUtils.getParentOfType(context.getInvocationElement(), ResultMap.class, true);
        if (resultMap == null) {
            return null;
        }
        return resultMap.getTypeValue().map(type -> JavaPsiService.getInstance(context.getProject()).findPsiClass(type)
                .map(psiClass -> Arrays.stream(psiClass.getAllFields()).filter(field -> o.equals(field.getName())).findFirst()
                        .orElse(null)).orElse(null)).orElse(null);
    }
}
