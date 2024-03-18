package com.msoft.mbi.cube.multi.renderers;


public class MascaraAntesRenderer implements MascaraRenderer {

    private String textoMascara;
    private MascaraRenderer renderer;

    public MascaraAntesRenderer(MascaraRenderer celulaDecorator, String textoMascara) {
        this.renderer = celulaDecorator;
        this.textoMascara = textoMascara;
    }

    public MascaraAntesRenderer(MascaraRenderer celulaDecorator) {
        this.renderer = celulaDecorator;
        this.textoMascara = "R$";
    }

    @Override
    public Object aplica(Object valor) {
        return this.textoMascara + this.renderer.aplica(valor);
    }

}
