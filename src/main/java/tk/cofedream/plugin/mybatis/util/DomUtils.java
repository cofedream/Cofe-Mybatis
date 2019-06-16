package tk.cofedream.plugin.mybatis.util;

import com.intellij.pom.PomTarget;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomTarget;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-01
 */
public final class DomUtils {

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
        DomElement domElement = DomUtil.getDomElement(element);
        if (requiredClass.isInstance(domElement)) {
            return ((T) domElement);
        }
        return DomUtil.getParentOfType(domElement, requiredClass, true);
    }

}
