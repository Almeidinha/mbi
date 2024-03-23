package com.msoft.mbi.cube.multi.renderers;


public class MaskBeforeRenderer implements MaskRenderer {

    private final String maskText;
    private final MaskRenderer renderer;

    public MaskBeforeRenderer(MaskRenderer cellDecorator, String maskText) {
        this.renderer = cellDecorator;
        this.maskText = maskText;
    }

    public MaskBeforeRenderer(MaskRenderer cellDecorator) {
        this.renderer = cellDecorator;
        this.maskText = "R$";
    }

    @Override
    public Object apply(Object valor) {
        return this.maskText + this.renderer.apply(valor);
    }

}
