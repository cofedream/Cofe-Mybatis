/*
 * Copyright (C) 2019 cofe
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mbsp.psi.impl;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mbsp.MbspTypes;
import tk.cofe.plugin.mbsp.psi.MbspReferenceExpression;
import tk.cofe.plugin.mbsp.psi.MbspReferenceProvider;

import java.util.ServiceLoader;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MbspPsiUtil {

    @Nullable
    public static PsiReference getReference(MbspReferenceExpression element) {
        return getReferenceProvider(MbspTypes.REFERENCE_EXPRESSION, element);
    }

    @Nullable
    public static <T extends PsiElement> PsiReference getReferenceProvider(IElementType elementType, T element) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(element.getClass().getClassLoader());
        for (MbspReferenceProvider provider : ServiceLoader.load(MbspReferenceProvider.class)) {
            if (provider.isSupported(elementType)) {
                Thread.currentThread().setContextClassLoader(classLoader);
                return provider.exec(element, getOriginElement(element));
            }
        }
        Thread.currentThread().setContextClassLoader(classLoader);
        return null;
    }

    private static PsiElement getOriginElement(final PsiElement element) {
        return InjectedLanguageManager.getInstance(element.getProject()).getInjectionHost(element);
    }

    //public static String getName(MbspExpression element) {
    //    ASTNode keyNode = element.getNode().findChildByType(MbspTypes.PLAIN);
    //    if (keyNode != null) {
    //        return keyNode.getText();
    //    } else {
    //        return null;
    //    }
    //}
    //
    //public static PsiElement setName(MbspExpression element, String newName) {
    //    ASTNode keyNode = element.getNode().findChildByType(MbspTypes.PLAIN);
    //    if (keyNode != null) {
    //
    //        MbspExpression property = MbspElementFactory.createProperty(element.getProject(), newName);
    //        ASTNode newKeyNode = property.getFirstChild().getNode();
    //        element.getNode().replaceChild(keyNode, newKeyNode);
    //    }
    //    return element;
    //}
    //
    //public static PsiElement getNameIdentifier(MbspExpression element) {
    //    ASTNode keyNode = element.getNode().findChildByType(MbspTypes.PLAIN);
    //    if (keyNode != null) {
    //        return keyNode.getPsi();
    //    } else {
    //        return null;
    //    }
    //}
}
