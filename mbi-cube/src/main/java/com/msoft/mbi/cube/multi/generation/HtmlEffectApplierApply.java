package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.renderers.MaskRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MaskLinkHTMLDynamicValueRenderer;

public class HtmlEffectApplierApply implements HtmlEffectApplier {

    @Override
    public String applyHtmlEffect(Object value, MaskRenderer effectDecorator) {
        return effectDecorator.apply(value).toString();
    }

    @Override
    public String applyDynamicHtmlEffect(Object printValue, String parameterValue, MaskLinkHTMLDynamicValueRenderer effectDecorator) {
        return effectDecorator.apply(printValue, parameterValue).toString();
    }
}
