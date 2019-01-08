package tk.cofedream.plugin.mybatis.utils;

import com.intellij.pom.PomTarget;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.util.xml.DomTarget;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-01
 */
public class DomUtils {


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

    //public static <T extends DomElement> Optional<String> getNamespaces(Class<T> elementClass) {
    //    return Optional.ofNullable(DomReflectionUtil.findAnnotationDFS(elementClass, Namespace.class)).flatMap(namespace -> Optional.of(namespace.value()));
    //}
}
