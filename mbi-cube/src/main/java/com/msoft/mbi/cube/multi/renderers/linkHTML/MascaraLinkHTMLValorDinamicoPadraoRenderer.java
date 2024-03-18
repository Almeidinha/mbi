package com.msoft.mbi.cube.multi.renderers.linkHTML;

public class MascaraLinkHTMLValorDinamicoPadraoRenderer extends MascaraLinkHTMLValorDinamicoRenderer {

    public MascaraLinkHTMLValorDinamicoPadraoRenderer() {
        super(null);
    }

    @Override
    public Object aplica(Object valor, String parametro1Inserir) {
        return valor;
    }

    @Override
    public Object aplica(Object valor) {
        return valor;
    }

}
