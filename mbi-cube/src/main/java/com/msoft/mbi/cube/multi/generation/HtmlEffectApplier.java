package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.renderers.MaskRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MaskLinkHTMLDynamicValueRenderer;

public interface HtmlEffectApplier {

    public String applyHtmlEffect(Object valor, MaskRenderer effectDecorator);

    public String applyDynamicHtmlEffect(Object printValue, String parameterValue, MaskLinkHTMLDynamicValueRenderer effectDecorator);
}
