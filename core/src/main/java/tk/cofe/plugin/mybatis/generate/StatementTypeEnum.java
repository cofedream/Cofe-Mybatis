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

package tk.cofe.plugin.mybatis.generate;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.GenericAttributeValue;
import tk.cofe.plugin.common.bundle.MyBatisBundle;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.model.tag.Delete;
import tk.cofe.plugin.mybatis.dom.model.tag.Insert;
import tk.cofe.plugin.mybatis.dom.model.tag.Select;
import tk.cofe.plugin.mybatis.dom.model.tag.Update;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.List;

/**
 * 声明类型
 *
 * @author : zhengrf
 * @date : 2019-06-23
 */
public enum StatementTypeEnum {
    SELECT(Annotation.SELECT, "Select") {
        @Override
        public Select addClassElement(Mapper mapper, PsiMethod method) {
            Select select = mapper.addSelect();
            GenericAttributeValue<PsiClass> resultTypeValue = select.getResultType();
            if (resultTypeValue != null) {
                List<String> resultType = MybatisUtils.getResultType(method.getReturnType());
                if (resultType.isEmpty()) {
                    Notifications.Bus.notify(new Notification(
                            MyBatisBundle.message("action.group.text"),
                            MyBatisBundle.message("action.generate.title"),
                            MyBatisBundle.message("action.generate.text", method.getName()),
                            NotificationType.WARNING));
                } else {
                    resultTypeValue.setStringValue(resultType.get(0));
                }
            }
            return select;
        }
    }, INSERT(Annotation.INSERT, "Insert") {
        @Override
        public Insert addClassElement(Mapper mapper, PsiMethod method) {
            return mapper.addInsert();
        }
    }, UPDATE(Annotation.UPDATE, "Update") {
        @Override
        public Update addClassElement(Mapper mapper, PsiMethod method) {
            return mapper.addUpdate();
        }
    }, DELETE(Annotation.DELETE, "Delete") {
        @Override
        public Delete addClassElement(Mapper mapper, PsiMethod method) {
            return mapper.addDelete();
        }
    },
    ;

    private String desc;

    private Annotation annotation;

    StatementTypeEnum(Annotation annotation, String desc) {
        this.annotation = annotation;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    /**
     * 执行创建元素动作,并返回创建的元素
     *
     * @param mapper Mapper
     * @param method Java方法
     * @return 要创建的元素
     */
    public abstract ClassElement addClassElement(Mapper mapper, PsiMethod method);

    /**
     * 指定元素动作
     *
     * @param mapper  Mapper
     * @param method  方法
     * @param project 当前项目
     */
    public void createStatement(Mapper mapper, PsiMethod method, Project project) {
        if (mapper == null || method == null || project == null) {
            return;
        }
        ClassElement element = addClassElement(mapper, method);
        XmlTag tag = element.getXmlTag();
        if (tag == null) {
            return;
        }
        element.setValue("\n");
        int offset = 0;
        GenericAttributeValue<PsiMethod> elementId = element.getId();
        if (elementId.getXmlAttributeValue() != null) {
            elementId.setStringValue(method.getName());
            offset = elementId.getXmlAttributeValue().getTextOffset();
        }
        NavigationUtil.activateFileWithPsiElement(tag, true);
        Editor xmlEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (null != xmlEditor) {
            xmlEditor.getCaretModel().moveToOffset(offset);
            xmlEditor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
        }
    }
}
