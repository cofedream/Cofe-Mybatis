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
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.bundle.MyBatisBundle;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Association;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.CollectionUtils;
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * property 属性
 *
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class PropertyConverter extends ResolvingConverter<PsiField> {

    private static final RowIcon PRIVATE_FIELD_ICON = new RowIcon(PlatformIcons.FIELD_ICON, PlatformIcons.PRIVATE_ICON);

    @Override
    public String getErrorMessage(@Nullable final String s, final ConvertContext context) {
        return MyBatisBundle.message("error.cannot.resolve.field.message", s);
    }

    @NotNull
    @Override
    public Collection<? extends PsiField> getVariants(ConvertContext context) {
        String propertyType = PropertyType.parse(context.getInvocationElement());
        if (propertyType == null) {
            return Collections.emptyList();
        }
        return JavaPsiService.getInstance(context.getProject()).findPsiClass(propertyType)
                .map(psiClass -> Arrays.stream(psiClass.getAllFields())
                        .filter(field -> !field.hasModifierProperty(PsiModifier.FINAL) || !field.hasModifierProperty(PsiModifier.STATIC))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Nullable
    @Override
    public LookupElement createLookupElement(PsiField psiMember) {
        return psiMember == null || psiMember.getName() == null ? null : LookupElementBuilder.create(psiMember.getName()).withIcon(PRIVATE_FIELD_ICON);
    }

    @Nullable
    @Override
    public PsiField fromString(@Nullable final String member, final ConvertContext context) {
        if (StringUtils.isBlank(member)) {
            return null;
        }
        String type = PropertyType.parse(context.getInvocationElement());
        if (StringUtils.isBlank(type)) {
            return null;
        }
        List<PsiField> fields = JavaPsiService.getInstance(context.getProject()).findPsiClass(type)
                .map(psiClass -> Arrays.asList(psiClass.getAllFields()))
                .orElse(Collections.emptyList());
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }
        for (PsiField field : fields) {
            if (member.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable final PsiField psiMember, final ConvertContext context) {
        return psiMember == null ? null : psiMember.getName();
    }

    @Override
    public void handleElementRename(final GenericDomValue<PsiField> genericValue, final ConvertContext context, final String newElementName) {
        super.handleElementRename(genericValue, context, newElementName);
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
        COLLECTION(tk.cofe.plugin.mybatis.dom.description.model.dynamic.Collection.class) {
            @Override
            String getType(DomElement domElement) {
                return ((tk.cofe.plugin.mybatis.dom.description.model.dynamic.Collection) domElement).getOfTypeValue().orElse(null);
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
