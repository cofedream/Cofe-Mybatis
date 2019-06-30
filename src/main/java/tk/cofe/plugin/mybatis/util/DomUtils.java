package tk.cofe.plugin.mybatis.util;

import com.intellij.pom.PomTarget;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomTarget;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-01
 */
public final class DomUtils extends DomUtil {

    public static Optional<DomTarget> resolveToDomTarget(@NotNull PsiElement element) {
        if (element instanceof DomTarget) {
            return Optional.of(((DomTarget) element));
        }
        if (element instanceof PomTargetPsiElement) {
            PomTarget target = ((PomTargetPsiElement) element).getTarget();
            return target instanceof DomTarget ? Optional.of(((DomTarget) target)) : Optional.empty();
        }
        return Optional.empty();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getTargetElement(PsiElement element, final Class<T> requiredClass) {
        DomElement domElement = DomUtils.getDomElement(element);
        if (requiredClass.isInstance(domElement)) {
            return ((T) domElement);
        }
        return DomUtils.getParentOfType(domElement, requiredClass, true);
    }

    /**
     * 获取 {@code GenericAttributeValue<String> } 属性值值
     * @param attributeValue 属性值对象
     * @return NULL 则返回 {@code Optional.empty()}
     */
    @NotNull
    public static Optional<String> getAttributeVlaue(@Nullable GenericAttributeValue<String> attributeValue) {
        if (attributeValue == null) {
            return Optional.empty();
        }
        return StringUtils.isBlank(attributeValue.getValue()) ? Optional.empty() : Optional.of(attributeValue.getValue().trim());
    }
}
