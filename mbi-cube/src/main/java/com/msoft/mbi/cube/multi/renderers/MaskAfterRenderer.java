package com.msoft.mbi.cube.multi.renderers;

public class MaskAfterRenderer implements MaskRenderer {

    private final String maskText;
    private final MaskRenderer renderer;

    public MaskAfterRenderer(MaskRenderer cellDecorator, String maskText) {
        this.renderer = cellDecorator;
        this.maskText = maskText;
    }

    @Override
    public Object apply(Object valor) {
        return this.renderer.apply(valor) + this.maskText;
    }

}
