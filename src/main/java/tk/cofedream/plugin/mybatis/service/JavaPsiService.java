package tk.cofedream.plugin.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.Processor;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.dom.mapper.model.ClassElement;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public interface JavaPsiService {
    static JavaPsiService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, JavaPsiService.class);
    }

    @NotNull
    Optional<PsiClass> findClass(@NotNull ClassElement element);

    @NotNull
    Optional<PsiMethod[]> findMethod(@NotNull ClassElement element);

    @NonNull
    Optional<PsiClass> getPsiClass(@NotNull String qualifiedName);

    void process(@NotNull PsiElement target, @NotNull Processor processor);
}
