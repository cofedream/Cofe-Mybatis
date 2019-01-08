package tk.cofedream.plugin.mybatis.dom.usage;

import com.intellij.find.findUsages.CustomUsageSearcher;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.project.DumbService;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.usages.TextChunk;
import com.intellij.usages.Usage;
import com.intellij.usages.UsagePresentation;
import com.intellij.usages.rules.PsiElementUsage;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author : zhengrf
 * @date : 2019-01-04
 */
public class MapperXmlUsageSearcher extends CustomUsageSearcher {
    @Override
    public void processElementUsages(@NotNull PsiElement element, @NotNull Processor<Usage> processor, @NotNull FindUsagesOptions options) {
        if (!(element instanceof PomTargetPsiElement)) {
            return;
        }
        DumbService.getInstance(element.getProject()).runReadActionInSmartMode(() -> {
            //if (!element.getLanguage().is(XMLLanguage.INSTANCE)) {
            //    return;
            //}
            //if (MapperUtils.isMapperXmlFile(element.getContainingFile())) {
            //    PsiElement context = element.getContext();
            //    XmlTag tag = PsiTreeUtil.getParentOfType(context, XmlTag.class, true);
            //    if (MapperUtils.isBaseStatementElement(tag)) {
            //        ClassElement classElement = (ClassElement) DomUtil.getDomElement(tag);
            //        Optional<PsiMethod[]> method = JavaPsiUtils.findMethod(element.getProject(), classElement);
            //        PsiMethod psiMethod = method.get()[0];
            //
            //        System.out.println(psiMethod.getName());
            //        //processor.process(new MyUsage(psiMethod));
            //        //processor.process();
            //        // 是增删拆改中的元素
            //    }
            //    //PsiElement parent = context.getParent();
            //    //PsiFile containingFile = element.getContainingFile();
            //    //MapperXml parentOfType = DomUtil.getParentOfType(domElement, MapperXml.class, true);
            //}
        });
    }

    private static class MyUsage implements PsiElementUsage, UsagePresentation {
        private PsiMethod targetMethod;

        MyUsage(PsiMethod targetMethod) {
            this.targetMethod = targetMethod;
        }

        @Override
        public PsiElement getElement() {
            return targetMethod.getNavigationElement();
        }

        @Override
        public boolean isNonCodeUsage() {
            return false;
        }

        @NotNull
        @Override
        public UsagePresentation getPresentation() {
            return this;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Nullable
        @Override
        public FileEditorLocation getLocation() {
            return null;
        }

        @Override
        public void selectInEditor() {

        }

        @Override
        public void highlightInEditor() {

        }

        @Override
        public void navigate(boolean requestFocus) {

        }

        @Override
        public boolean canNavigate() {
            return false;
        }

        @Override
        public boolean canNavigateToSource() {
            return true;
        }

        @NotNull
        @Override
        public TextChunk[] getText() {
            return ContainerUtil.newArrayList().toArray(TextChunk.EMPTY_ARRAY);
        }

        @NotNull
        @Override
        public String getPlainText() {
            return "null";
        }

        @Override
        public Icon getIcon() {
            return null;
        }

        @Override
        public String getTooltipText() {
            return "null";
        }
    }
}
