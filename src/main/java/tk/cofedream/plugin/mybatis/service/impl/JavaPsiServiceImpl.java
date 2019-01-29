package tk.cofedream.plugin.mybatis.service.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tukaani.xz.rangecoder.RangeEncoderToBuffer;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Mapper;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;
import tk.cofedream.plugin.mybatis.service.MapperService;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public class JavaPsiServiceImpl implements JavaPsiService {
    private final Project project;
    private final MapperService mapperService;
    private final JavaPsiFacade javaPsiFacade;

    public JavaPsiServiceImpl(Project project) {
        this.project = project;
        this.mapperService = MapperService.getInstance(project);
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(@NotNull PsiElement target, @NotNull Processor processor) {
        if (target instanceof PsiMethod) {
            process((PsiMethod) target, processor);
        } else if (target instanceof PsiClass) {
            process((PsiClass) target, processor);
        }
    }

    public void process(@NotNull PsiMethod psiMethod, @NotNull Processor<ClassElement> processor) {
        PsiClass psiClass = psiMethod.getContainingClass();
        if (psiClass == null) {
            return;
        }
        mapperService.findMapperXmls(psiClass).forEach(mapperXml -> mapperXml.getClassElements().forEach(classElement -> {
            classElement.getIdValue().ifPresent(id -> {
                if (id.equals(psiMethod.getName())) {
                    processor.process(classElement);
                }
            });
        }));
    }

    public void process(@NotNull PsiClass psiClass, @NotNull Processor<Mapper> processor) {
        mapperService.findMapperXmls(psiClass).forEach(mapperXml -> mapperXml.getNamespaceValue().ifPresent(qualifiedName -> {
            if (qualifiedName.equals(psiClass.getQualifiedName())) {
                processor.process(mapperXml);
            }
        }));
    }

    @NonNull
    @Override
    public Optional<PsiClass> getPsiClass(@NotNull String qualifiedName) {
        return Optional.ofNullable(javaPsiFacade.findClass(qualifiedName, GlobalSearchScope.projectScope(project)));
    }

    @NotNull
    @Override
    public Optional<PsiMethod> findMethod(@Nullable ClassElement element) {
        return findMethods(element).flatMap(psiMethods -> Optional.of(psiMethods[0]));
    }

    @NotNull
    @Override
    public Optional<PsiMethod[]> findMethods(@Nullable ClassElement element) {
        if (element == null || !element.getIdValue().isPresent()) {
            return Optional.empty();
        }
        return element.getNamespaceValue().flatMap(qualifiedName ->
                getPsiClass(qualifiedName).flatMap(psiClass ->
                        element.getIdValue().flatMap(id -> Optional.of(psiClass.findMethodsByName(id, false)))));
    }

    @NotNull
    @Override
    public Optional<PsiMethod[]> findMethod(@NotNull Mapper mapper, String methodName) {
        return mapper.getNamespaceValue().flatMap(namespace -> getPsiClass(namespace).flatMap(psiClass -> Optional.of(psiClass.findMethodsByName(methodName, false))));
    }

    @NotNull
    @Override
    public Optional<PsiClass> findClass(@NotNull ClassElement element) {
        return element.getNamespaceValue().flatMap(this::getPsiClass);
    }
}
