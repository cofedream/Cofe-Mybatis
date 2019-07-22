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

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifier;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Association;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.service.JavaPsiService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class PropertyConverter extends ResolvingConverter.StringConverter {

    private static final RowIcon PRIVATE_FIELD_ICON = new RowIcon(PlatformIcons.FIELD_ICON, PlatformIcons.PRIVATE_ICON);

    @NotNull
    @Override
    public Collection<? extends String> getVariants(ConvertContext context) {
        String propertyType = PropertyType.parse(context.getInvocationElement());
        if (propertyType == null) {
            return Collections.emptyList();
        }
        return JavaPsiService.getInstance(context.getProject()).findPsiClass(propertyType)
                .map(psiClass -> Arrays.stream(psiClass.getAllFields())
                        .filter(field -> !field.hasModifierProperty(PsiModifier.FINAL) || !field.hasModifierProperty(PsiModifier.STATIC))
                        .map(NavigationItem::getName)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Nullable
    @Override
    public LookupElement createLookupElement(String property) {
        return property == null ? null : LookupElementBuilder.create(property).withIcon(PRIVATE_FIELD_ICON);
    }

    @Nullable
    @Override
    public PsiElement resolve(String o, ConvertContext context) {
        String propertyType = PropertyType.parse(context.getInvocationElement());
        if (propertyType == null) {
            return null;
        }
        return JavaPsiService.getInstance(context.getProject()).findPsiClass(propertyType)
                .map(psiClass -> Arrays.stream(psiClass.getAllFields()).filter(field -> o.equals(field.getName())).findFirst()
                        .orElse(null)).orElse(null);
    }

    private enum PropertyType {
        RESULT_MAP(ResultMap.class) {
            @Override
            String getType(DomElement domElement) {
                return ((ResultMap) domElement).getTypeValue().orElse(null);
            }
        },
        ASSOCIATION(Association.class) {
            @Override
            String getType(DomElement domElement) {
                return ((Association) domElement).getJavaTypeValue().orElse(null);
            }
        },
        COLLECTION(tk.cofe.plugin.mybatis.dom.description.model.tag.Collection.class) {
            @Override
            String getType(DomElement domElement) {
                return ((tk.cofe.plugin.mybatis.dom.description.model.tag.Collection) domElement).getOfTypeValue().orElse(null);
            }
        },
        ;

        private Class<?> typeClass;

        PropertyType(final Class<?> typeClass) {
            this.typeClass = typeClass;
        }

        public static String parse(final DomElement domElement) {
            if (domElement == null) {
                return null;
            }
            for (DomElement curElement = domElement.getParent() == null ? domElement.getParent() : domElement.getParent().getParent();
                 curElement != null;
                 curElement = curElement.getParent()) {
                for (PropertyType type : values()) {
                    if (type.getTypeClass().isInstance(curElement)) {
                        return type.getType(curElement);
                    }
                }
            }
            return null;
        }

        public Class<?> getTypeClass() {
            return typeClass;
        }

        abstract String getType(DomElement domElement);
    }
}
