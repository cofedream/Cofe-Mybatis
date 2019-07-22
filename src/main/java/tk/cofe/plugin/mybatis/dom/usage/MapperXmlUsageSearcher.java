/*
 * Copyright (C) 2019 cofe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mybatis.dom.usage;

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
