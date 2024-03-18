package com.msoft.mbi.cube.multi.renderers.linkHTML;

import java.io.Serial;

public class MascaraLinkHTMLValorDinamicoPadraoRenderer extends MascaraLinkHTMLValorDinamicoRenderer {
    @Serial
    private static final long serialVersionUID = -3879648374368263054L;

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
