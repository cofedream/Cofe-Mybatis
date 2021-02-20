// /*
//  * Copyright (C) 2019-2021 cofe
//  *
//  * This program is free software: you can redistribute it and/or modify
//  * it under the terms of the GNU General Public License as published by
//  * the Free Software Foundation, either version 3 of the License, or
//  * (at your option) any later version.
//  *
//  * This program is distributed in the hope that it will be useful,
//  * but WITHOUT ANY WARRANTY; without even the implied warranty of
//  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  * GNU General Public License for more details.
//  *
//  * You should have received a copy of the GNU General Public License
//  * along with this program.  If not, see <https://www.gnu.org/licenses/>.
//  */
//
// package tk.cofe.plugin.mybatis.completion;
//
// import com.intellij.codeInsight.completion.CompletionContributor;
// import com.intellij.codeInsight.completion.CompletionParameters;
// import com.intellij.codeInsight.completion.CompletionProvider;
// import com.intellij.codeInsight.completion.CompletionResultSet;
// import com.intellij.codeInsight.completion.CompletionType;
// import com.intellij.codeInsight.lookup.LookupElementBuilder;
// import com.intellij.patterns.PsiElementPattern;
// import com.intellij.psi.PsiElement;
// import com.intellij.util.ProcessingContext;
// import org.jetbrains.annotations.NotNull;
// import tk.cofe.plugin.mbsp.psi.MbspExpression;
// import tk.cofe.plugin.mbsp.psi.MbspJdbcTypeConfig;
// import tk.cofe.plugin.mbsp.psi.MbspModeConfig;
// import tk.cofe.plugin.mbsp.psi.MbspParamConfig;
// import tk.cofe.plugin.mbsp.psi.MbspReferenceExpression;
//
// import static com.intellij.patterns.PlatformPatterns.psiElement;
// import static tk.cofe.plugin.mbsp.MbspKeyword.JdbcType;
// import static tk.cofe.plugin.mbsp.MbspKeyword.Mode;
//
// /**
//  * @author : zhengrf
//  * @date : 2021-02-20
//  */
// public class MbspKeywordCompletionContributor extends CompletionContributor {
//
//     private static final PsiElementPattern.Capture<PsiElement> MODE_EXPRESSION = psiElement().inside(MbspModeConfig.class);
//     private static final PsiElementPattern.Capture<PsiElement> JDBC_TYPE_EXPRESSION = psiElement().inside(MbspJdbcTypeConfig.class);
//     //
//     private static final PsiElementPattern.Capture<PsiElement> PARAM_CONFIG_EXPRESSION = psiElement()
//             // .afterLeaf(",");
//             .afterLeafSkipping(psiElement(MbspExpression.class),psiElement().withText(","));
//             // .inside(MbspParamConfigExpression.class)
//             // .andNot(MODE_EXPRESSION)
//             // .andNot(JDBC_TYPE_EXPRESSION);
//
//     public MbspKeywordCompletionContributor() {
//         installMode();
//         installJdbcType();
//         installParamConfig();
//     }
//
//     private void installMode() {
//         extendCompletion(MODE_EXPRESSION, Mode.STRINGS);
//     }
//
//     private void installJdbcType() {
//         extendCompletion(JDBC_TYPE_EXPRESSION, JdbcType.STRINGS);
//     }
//
//     private void installParamConfig() {
//         extendCompletion(PARAM_CONFIG_EXPRESSION, "jdbcType");
//     }
//
//     private void extendCompletion(final PsiElementPattern.Capture<PsiElement> pattern,
//                                   final String... keywords) {
//         extend(CompletionType.BASIC, pattern, new CompletionProvider<CompletionParameters>() {
//             @Override
//             protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
//                 for (String keyword : keywords) {
//                     final LookupElementBuilder builder = LookupElementBuilder.create(keyword).bold();
//                     result.addElement(builder);
//                 }
//             }
//         });
//     }
// }
