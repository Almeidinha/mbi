package com.msoft.mbi.cube.multi.renderers.linkHTML;

public class MaskLinkHTMLDynamicValueRenderer extends MascLinkHTMLTextRenderer {


    public MaskLinkHTMLDynamicValueRenderer(LinkHTMLDynamicText link) {
        super(link);
    }

    public Object apply(Object valor, String parameter) {
        this.link.getParameters().put(((LinkHTMLDynamicText) this.link).getDynamicParameterName(), parameter);
        this.link.setContent(valor.toString());
        return super.apply(valor);
    }

}
