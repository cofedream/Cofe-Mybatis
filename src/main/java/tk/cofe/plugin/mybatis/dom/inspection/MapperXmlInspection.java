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

package tk.cofe.plugin.mybatis.dom.inspection;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;

/**
 * Mapper Xml检查
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class MapperXmlInspection extends BasicDomElementsInspection<Mapper> {
    public MapperXmlInspection() {
        super(Mapper.class);
    }

    @Override
    public void checkFileElement(DomFileElement<Mapper> domFileElement, DomElementAnnotationHolder holder) {
        super.checkFileElement(domFileElement, holder);
    }

    @Override
    protected boolean shouldCheckResolveProblems(GenericDomValue value) {
        return super.shouldCheckResolveProblems(value);
    }

    @Override
    protected void checkDomElement(DomElement element, DomElementAnnotationHolder holder, DomHighlightingHelper helper) {
        super.checkDomElement(element, holder, helper);
    }

    @NotNull
    @Override
    public String getShortName() {
        return "MapperXmlInspection";
    }
}