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

package tk.cofe.plugin.mybatis.generate;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Delete;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Insert;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Select;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Update;
import tk.cofe.plugin.mybatis.util.CollectionUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;

import java.util.List;

/**
 * 声明类型
 * @author : zhengrf
 * @date : 2019-06-23
 */
public enum StatementTypeEnum {
    SELECT("Select") {
        @Override
        public Select addClassElement(@NotNull Mapper mapper, PsiMethod method) {
            Select select = mapper.addSelect();
            GenericAttributeValue<String> resultTypeValue = select.getResultType();
            if (resultTypeValue != null) {
                List<String> resultType = PsiMybatisUtils.getResultType(method.getReturnType());
                if (CollectionUtils.isNotEmpty(resultType)) {
                    resultTypeValue.setStringValue(resultType.get(0));
                }
            }
            return select;
        }
    }, INSERT("Insert") {
        @Override
        public Insert addClassElement(@NotNull Mapper mapper, PsiMethod method) {
            return mapper.addInsert();
        }
    }, UPDATE("Update") {
        @Override
        public Update addClassElement(@NotNull Mapper mapper, PsiMethod method) {
            return mapper.addUpdate();
        }
    }, DELETE("Delete") {
        @Override
        public Delete addClassElement(@NotNull Mapper mapper, PsiMethod method) {
            return mapper.addDelete();
        }
    },
    ;

    private String desc;

    StatementTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 执行创建元素动作,并返回创建的元素
     * @param mapper Mapper
     * @param method Java方法
     * @return 要创建的元素
     */
    public abstract ClassElement addClassElement(@NotNull Mapper mapper, PsiMethod method);

    /**
     * 指定元素动作
     * @param mapper  Mapper
     * @param method  方法
     * @param project 当前项目
     */
    public void processCreateStatement(Mapper mapper, PsiMethod method, @NotNull Project project) {
        ClassElement element = addClassElement(mapper, method);
        XmlTag tag = element.getXmlTag();
        if (tag == null) {
            return;
        }
        element.setValue("\n");
        int offset = 0;
        GenericAttributeValue<String> selectId = element.getId();
        if (selectId.getXmlAttributeValue() != null) {
            selectId.setStringValue(method.getName());
            offset = selectId.getXmlAttributeValue().getTextOffset();
        }
        NavigationUtil.activateFileWithPsiElement(tag, true);
        Editor xmlEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (null != xmlEditor) {
            xmlEditor.getCaretModel().moveToOffset(offset);
            xmlEditor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
        }
    }
}
