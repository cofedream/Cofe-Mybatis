/*
 * Copyright (C) 2019-2021 cofe
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

package tk.cofe.plugin.mybatis.psi.reference;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbl.MblTypes;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author : zhengrf
 * @date : 2020-01-19
 */
public class MblReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
        // registrar.registerReferenceProvider(psiElement(MblTypes.REFERENCE_EXPRESSION), new MblReferenceProvider());
        registrar.registerReferenceProvider(psiElement(MblTypes.REFERENCE_EXPRESSION), new PsiReferenceProvider() {
        // registrar.registerReferenceProvider(psiElement(LeafElement.class), new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                return new PsiReference[0];
            }
        });
    }
}
