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

package tk.cofe.plugin.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;

import java.util.List;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public interface MapperService {
    static MapperService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MapperService.class);
    }

    /**
     * 判断是否为 Mapper 接口文件
     * @param mapperClass Class文件
     */
    boolean isMapperClass(@NotNull PsiClass mapperClass);

    /**
     * 判断 MapperInterface 是否存在与之对应的 Mapper 文件
     * @param mapperClass MapperInterface
     * @return 匹配到的 Mapper Xml 文件
     */
    @NotNull
    List<Mapper> findMapperXmls(@NotNull PsiClass mapperClass);

    /**
     * 根据PsiClass 获取 Mapper Xml中的所有 Statement
     * @param mapperClass Mapper Interface
     * @return Mapper Statement
     */
    @NotNull
    List<ClassElement> findStatemtnts(@NotNull PsiClass mapperClass);

    /**
     * 获取Mapper Dom 文件
     * @return Mapper文件
     */
    @NotNull
    List<Mapper> findAllMappers();

    /**
     * 查找 Dom 文件
     * @param <T>   {@code T extends DomElement}
     * @param clazz Domm描述类 {@code extends DomElement}
     * @return 匹配的Dom文件
     */
    <T extends DomElement> List<T> findDomElements(@NotNull Class<T> clazz);

    /**
     * 找到 PsiMethod 对应的Mapper Statement
     * @param method 方法
     * @return Statement 集合
     */
    @NotNull
    Optional<ClassElement> findStatement(@NotNull PsiMethod method);

    /**
     * 查询PsiMethod对应的Mapper Statement是否存在
     * @param method 方法
     * @return true 存在, false 不存在
     */
    boolean existStatement(@NotNull PsiMethod method);

}
