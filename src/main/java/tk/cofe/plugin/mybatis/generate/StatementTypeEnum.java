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
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.bundle.MyBatisBundle;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Delete;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Insert;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Select;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Update;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;

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
        public Select addClassElement(@NotNull Mapper mapper, PsiMethod method) {
            Select select = mapper.addSelect();
            GenericAttributeValue<PsiClass> resultTypeValue = select.getResultType();
            if (resultTypeValue != null) {
                List<String> resultType = PsiMybatisUtils.getResultType(method.getReturnType());
                if (!resultType.isEmpty()) {
                    resultTypeValue.setStringValue(resultType.get(0));
                } else {
                    Notification mybatis = new Notification(
                            MyBatisBundle.message("action.group.text"),
                            MyBatisBundle.message("action.generate.title"),
                            MyBatisBundle.message("action.generate.text", method.getName()),
                            NotificationType.WARNING);
                    Notifications.Bus.notify(mybatis);
                }
            }
            return select;
        }
    }, INSERT(Annotation.INSERT, "Insert") {
        @Override
        public Insert addClassElement(@NotNull Mapper mapper, PsiMethod method) {
            return mapper.addInsert();
        }
    }, UPDATE(Annotation.UPDATE, "Update") {
        @Override
        public Update addClassElement(@NotNull Mapper mapper, PsiMethod method) {
            return mapper.addUpdate();
        }
    }, DELETE(Annotation.DELETE, "Delete") {
        @Override
        public Delete addClassElement(@NotNull Mapper mapper, PsiMethod method) {
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
    public abstract ClassElement addClassElement(@NotNull Mapper mapper, PsiMethod method);

    /**
     * 指定元素动作
     *
     * @param mapper   Mapper
     * @param psiClass 接口类
     * @param method   方法
     * @param project  当前项目
     */
    public void createStatement(Mapper mapper, final PsiClass psiClass, PsiMethod method, @NotNull Project project) {
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
