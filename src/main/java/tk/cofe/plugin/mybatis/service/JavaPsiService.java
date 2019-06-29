package tk.cofe.plugin.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Mapper;

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
    Optional<PsiClass> findPsiClass(@NotNull ClassElement element);

    @NotNull
    Optional<PsiMethod> findPsiMethod(@Nullable ClassElement element);

    @NotNull
    Optional<PsiMethod[]> findPsiMethods(@Nullable ClassElement element);

    @NotNull
    Optional<PsiMethod[]> findPsiMethod(@NotNull Mapper mapper, String methodName);

    /**
     * 查找PsiClass
     * @param qualifiedName 类全限定名
     * @return PsiClass
     */
    @NotNull
    Optional<PsiClass> findPsiClass(@NotNull String qualifiedName);

    void process(@NotNull PsiElement target, @NotNull Processor processor);

    /**
     * 导包
     * @param file          Java类文件
     * @param qualifiedName 包名
     */
    void importClass(PsiJavaFile file, String qualifiedName);

    /**
     * 给元素添加注解
     * @param psiModifierListOwner 目标元素
     * @param annotationText       注解文本
     */
    void addAnnotation(PsiModifierListOwner psiModifierListOwner, String annotationText);
}
