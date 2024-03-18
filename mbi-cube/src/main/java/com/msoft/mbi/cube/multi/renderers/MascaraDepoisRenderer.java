package com.msoft.mbi.cube.multi.renderers;

public class MascaraDepoisRenderer implements MascaraRenderer {

    private String textoMascara;
    private MascaraRenderer renderer;

    public MascaraDepoisRenderer(MascaraRenderer celulaDecorator, String textoDepois) {
        this.renderer = celulaDecorator;
        this.textoMascara = textoDepois;
    }

    @Override
    public Object aplica(Object valor) {
        return this.renderer.aplica(valor) + this.textoMascara;
    }

}
