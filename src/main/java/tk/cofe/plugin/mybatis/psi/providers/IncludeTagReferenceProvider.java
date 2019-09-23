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

package tk.cofe.plugin.mybatis.psi.providers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.ElementPresentationManager;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Sql;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhengrf
 * @date : 2019-01-17
 */
public class IncludeTagReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return new PsiReference[] {new ResultTypeAttributeReference(element)};
    }

    /**
     * @author : zhengrf
     * @date : 2019-01-05
     */
    private static class ResultTypeAttributeReference extends PsiReferenceBase.Poly<PsiElement> {
        ResultTypeAttributeReference(@NotNull PsiElement element) {
            super(element);
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean incompleteCode) {
            XmlAttributeValue originalElement = (XmlAttributeValue) myElement;
            Mapper mapper = MybatisUtils.getMapper(originalElement);
            if (mapper == null) {
                return new ResolveResult[0];
            }
            List<Sql> sqls = mapper.getSqls();
            List<ResolveResult> result = new ArrayList<>();
            sqls.forEach(sql -> sql.getIdValue().ifPresent(id -> {
                if (id.equals(originalElement.getValue())) {
                    if (sql.getXmlElement() != null) {
                        result.add(new PsiElementResolveResult(sql.getXmlElement()));
                    }
                }
            }));
            return result.toArray(new ResolveResult[0]);
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            Mapper mapper = MybatisUtils.getMapper(((XmlAttributeValue) myElement));
            if (mapper == null) {
                return new Object[0];
            }
            return ElementPresentationManager.getInstance().createVariants(mapper.getSqls());
        }

    }

}
