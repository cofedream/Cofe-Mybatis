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

package tk.cofe.plugin.mybatis.service.impl;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTypesUtil;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.PsiJavaUtils;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.service.TypeAliasService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.*;

/**
 * @author : zhengrf
 * @date : 2021-03-24
 */
public final class TypeAliasServiceImpl implements TypeAliasService {
    /**
     * 别名的全限定名
     */
    private static final Map<String, String> TYPE_ALIASES_CANONICAL_TEXT = new HashMap<>();

    private static final Map<String, List<String>> TYPE_LOOKUP = new HashMap<>();
    private static final Map<String, PsiPrimitiveType> ALIASES_TYPE = new HashMap<>();

    static {
        registerAlias("string", String.class);

        registerAlias("byte", Byte.class);
        registerAlias("long", Long.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("boolean", Boolean.class);

        registerAlias("byte[]", Byte[].class);
        registerAlias("long[]", Long[].class);
        registerAlias("short[]", Short[].class);
        registerAlias("int[]", Integer[].class);
        registerAlias("integer[]", Integer[].class);
        registerAlias("double[]", Double[].class);
        registerAlias("float[]", Float[].class);
        registerAlias("boolean[]", Boolean[].class);

        registerAlias("_byte", byte.class);
        registerAlias("_long", long.class);
        registerAlias("_short", short.class);
        registerAlias("_int", int.class);
        registerAlias("_integer", int.class);
        registerAlias("_double", double.class);
        registerAlias("_float", float.class);
        registerAlias("_boolean", boolean.class);

        registerAlias("_byte[]", byte[].class);
        registerAlias("_long[]", long[].class);
        registerAlias("_short[]", short[].class);
        registerAlias("_int[]", int[].class);
        registerAlias("_integer[]", int[].class);
        registerAlias("_double[]", double[].class);
        registerAlias("_float[]", float[].class);
        registerAlias("_boolean[]", boolean[].class);

        registerAlias("date", Date.class);
        registerAlias("decimal", BigDecimal.class);
        registerAlias("bigdecimal", BigDecimal.class);
        registerAlias("biginteger", BigInteger.class);
        registerAlias("object", Object.class);

        registerAlias("date[]", Date[].class);
        registerAlias("decimal[]", BigDecimal[].class);
        registerAlias("bigdecimal[]", BigDecimal[].class);
        registerAlias("biginteger[]", BigInteger[].class);
        registerAlias("object[]", Object[].class);

        registerAlias("map", Map.class);
        registerAlias("hashmap", HashMap.class);
        registerAlias("list", List.class);
        registerAlias("arraylist", ArrayList.class);
        registerAlias("collection", Collection.class);
        registerAlias("iterator", Iterator.class);

        registerAlias("ResultSet", ResultSet.class);

        registerAlias(PsiType.BYTE, "_byte", "_byte[]");
        registerAlias(PsiType.LONG, "_long", "_long[]");
        registerAlias(PsiType.SHORT, "_short", "_short[]");
        registerAlias(PsiType.INT, "_int", "_int[]");
        registerAlias(PsiType.INT, "_integer", "_integer[]");
        registerAlias(PsiType.DOUBLE, "_double", "_double[]");
        registerAlias(PsiType.FLOAT, "_float", "_float[]");
        registerAlias(PsiType.BOOLEAN, "_boolean", "_boolean[]");
    }

    private final Project project;

    public TypeAliasServiceImpl(Project project) {
        this.project = project;
    }

    private static void registerAlias(PsiPrimitiveType psiType, String... aliases) {
        for (String alias : aliases) {
            if (StringUtil.isEmptyOrSpaces(alias)) {
                continue;
            }
            ALIASES_TYPE.put(alias.toLowerCase(Locale.ENGLISH), psiType);
        }
    }

    private static void registerAlias(String alias, Class<?> aClass) {
        TYPE_ALIASES_CANONICAL_TEXT.put(alias, PsiTypesUtil.boxIfPossible(aClass.getTypeName()));
        TYPE_LOOKUP.compute(aClass.getTypeName(), (key, value) -> {
            (value = value == null ? new LinkedList<>() : value).add(alias);
            return value;
        });
    }

    @Override
    public String getAliasTypeCanonicalText(String alias) {
        return TYPE_ALIASES_CANONICAL_TEXT.get(alias);
    }

    @Override
    public PsiClass getAliasPsiClass(String alias) {
        final JavaPsiService instance = JavaPsiService.getInstance(project);
        final String aliasTypeCanonicalText = getAliasTypeCanonicalText(alias);
        return Optional.ofNullable(aliasTypeCanonicalText)
                .flatMap(instance::findPsiClass)
                .orElseGet(() -> {
                    if (StringUtil.isNotEmpty(aliasTypeCanonicalText) && aliasTypeCanonicalText.contains(".")) {
                        return instance.findPsiClass(aliasTypeCanonicalText.replace("[]", "")).orElse(null);
                    }
                    return null;
                });
    }

    @Override
    public boolean isPsiPrimitiveTypeAlias(String alias) {
        return ALIASES_TYPE.containsKey(alias);
    }

    @Override
    public List<String> getTypeLookup(String text) {
        return TYPE_LOOKUP.getOrDefault(text, Collections.emptyList());
    }

    // @Override
    // public Map<String, PsiClass> findAllRegiserType() {
    //     Map<String, PsiClass> all = new HashMap<>();
    //     final Collection<VirtualFile> files = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));
    //     final PsiManager psiManager = PsiManager.getInstance(project);
    //     for (VirtualFile file : files) {
    //         PsiJavaFile javaFile = (PsiJavaFile) psiManager.findFile(file);
    //         if (javaFile != null) {
    //             for (PsiClass aClass : javaFile.getClasses()) {
    //                 if (PsiJavaUtils.hasAnnotation(aClass, Annotation.ALIAS)) {
    //                     Optional.ofNullable(Annotation.ALIAS.getValue(aClass))
    //                             .map(Annotation.Value::getValue)
    //                             .ifPresent(value -> all.put(value, aClass));
    //                 }
    //             }
    //         }
    //     }
    //     return all;
    // }
}
