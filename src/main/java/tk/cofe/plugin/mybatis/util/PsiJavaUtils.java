package tk.cofe.plugin.mybatis.util;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;

import java.util.Collection;

/**
 * JavaPsi工具
 * @author : zhengrf
 * @date : 2019-01-01
 */
public final class PsiJavaUtils {

    /**
     * 判断 PsiElement 是否为接口
     * @param psiElement 元素
     * @return 如果是接口则返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isInterface(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiClass && ((PsiClass) psiElement).isInterface();
    }

    /**
     * 判断 PsiElement 是否为类方法
     * @param psiElement 元素
     * @return 如果是接口则返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isInterfaceMethod(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiMethod && isElementWithinInterface(psiElement);
    }

    private static boolean isElementWithinInterface(@Nullable PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass parentOfType = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return parentOfType != null && parentOfType.isInterface();
    }

    /**
     * 判断是否有指定注解
     * @param target     目标元素
     * @param annotation 目标注解
     * @return true 有指定注解，false 没有指定注解
     */
    public static boolean hasAnnotation(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        PsiModifierList modifierList = target.getModifierList();
        return modifierList != null && modifierList.hasAnnotation(annotation.getQualifiedName());
    }

    /**
     * 判断是否有指定注解
     * @param target     目标元素
     * @param annotations 目标注解集合
     * @return true 有指定注解，false 没有指定注解
     */
    public static boolean hasAnnotations(@NotNull PsiModifierListOwner target, @NotNull Collection<Annotation> annotations) {
        return annotations.stream().anyMatch(annotation -> hasAnnotation(target, annotation));
    }

    /**
     * 判断是否已经导入目标类
     * @param file          Java类文件
     * @param qualifiedName 类全限定名
     * @return true 已导入，false 未导入
     */
    public static boolean hasImportClass(@NotNull PsiJavaFile file, @NotNull String qualifiedName) {
        PsiImportList importList = file.getImportList();
        if (importList == null) {
            return false;
        }
        return importList.findSingleClassImportStatement(qualifiedName) != null;
    }

    /**
     * 导入类到指定Java文件
     * @param file     Java文件
     * @param psiClass 要导入的类
     */
    public static void importClass(@NotNull PsiJavaFile file, @NotNull PsiClass psiClass) {
        file.importClass(psiClass);
    }

    /**
     * 从当前编辑框获取当前光标所在元素
     * @param editor 编辑框
     * @return 当前光标所在元素
     */
    @Nullable
    public static PsiElement getElement(@NotNull Editor editor) {
        return PsiUtilBase.getElementAtCaret(editor);
    }

    /**
     * 从当前编辑框获取当前光标所在目标元素
     * @param editor 编辑框
     * @param target 目标元素
     * @return 当前光标所在元素
     */
    @Nullable
    public static <T extends PsiElement> T getElement(@NotNull Editor editor, @NotNull Class<T> target) {
        PsiElement element = PsiUtilBase.getElementAtCaret(editor);
        if (element == null) {
            return null;
        }
        return PsiTreeUtil.getParentOfType(element, target);
    }

}