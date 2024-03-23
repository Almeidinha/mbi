package com.msoft.mbi.cube.multi.renderers.linkHTML;

public class MaskLinkHTMLDefaultDynamicValueRenderer extends MaskLinkHTMLDynamicValueRenderer {

    public MaskLinkHTMLDefaultDynamicValueRenderer() {
        super(null);
    }

    @Override
    public Object apply(Object valor, String parameter) {
        return valor;
    }

    @Override
    public Object apply(Object valor) {
        return valor;
    }

}
