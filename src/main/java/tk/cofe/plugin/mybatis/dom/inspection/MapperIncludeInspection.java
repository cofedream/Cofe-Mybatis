package tk.cofe.plugin.mybatis.dom.inspection;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.Mapper;

/**
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class MapperIncludeInspection extends BasicDomElementsInspection<Mapper> {
    public MapperIncludeInspection() {
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
        return "MapperIncludeInspection";
    }
}
