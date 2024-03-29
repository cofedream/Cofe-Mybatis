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

package tk.cofe.plugin.mybatis.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.util.PsiEditorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.bundle.MyBatisBundle;
import tk.cofe.plugin.mybatis.generate.StatementGenerator;
import tk.cofe.plugin.mybatis.service.MapperService;

/**
 * Mapper Interface 检查,<a href="http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/code_inspections_and_intentions.html">详情</a>
 *
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class MapperInterfaceMethodInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final String STATEMENT_REF_NOT_DEFINED = "Statement #ref not defined";

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(@NotNull final PsiMethod method, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        PsiClass psiClass = method.getContainingClass();
        if (psiClass == null) {
            return null;
        }
        if (method.hasModifierProperty(PsiModifier.PRIVATE) // 非私有方法
                || method.hasModifierProperty(PsiModifier.DEFAULT) // 非默认方法
                || method.hasModifierProperty(PsiModifier.STATIC) // 非静态方法
        ) {
            return null;
        }
        MapperService mapperService = MapperService.getInstance(method.getProject());
        if (mapperService.isMapperClass(psiClass) // 非Mapper接口
                && !mapperService.existStatement(method) // 缺失映射
        ) {
            // 不存在Method对应的Statement则提示
            PsiIdentifier nameIdentifier = method.getNameIdentifier();
            if (nameIdentifier != null) {
                return new ProblemDescriptor[] {problemDescriptor(method, manager, isOnTheFly, nameIdentifier)};
            }
        }
        return null;
    }

    private ProblemDescriptor problemDescriptor(final PsiMethod method, final InspectionManager manager, final boolean isOnTheFly, final PsiIdentifier nameIdentifier) {
        return manager.createProblemDescriptor(nameIdentifier, STATEMENT_REF_NOT_DEFINED, generateStatement(method), ProblemHighlightType.ERROR, isOnTheFly);
    }

    /**
     * 生成标签
     */
    private LocalQuickFix generateStatement(@NotNull final PsiMethod method) {
        final String name = MyBatisBundle.message("action.generate.intention", "statement");
        return new LocalQuickFix() {
            @Override
            public @IntentionName @NotNull String getName() {
                return name;
            }

            @Override
            public @IntentionFamilyName @NotNull String getFamilyName() {
                return name;
            }

            @Override
            public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
                Editor editor = PsiEditorUtil.getInstance().findEditorByPsiElement(method);
                if (editor == null) {
                    return;
                }
                StatementGenerator.generator(project, editor, method.getContainingFile(), method);
            }
        };
    }

}
