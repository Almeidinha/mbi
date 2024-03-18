package com.msoft.mbi.cube.multi.renderers.linkHTML;

import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;

import java.io.Serial;

public class MascaraLinkHTMLTextoRenderer implements MascaraRenderer {
    @Serial
    private static final long serialVersionUID = 2455184977454409017L;

    protected LinkHTMLTexto linkAplicar;

    public MascaraLinkHTMLTextoRenderer(LinkHTMLTexto linkaplicar) {
        this.linkAplicar = linkaplicar;
    }

    @Override
    public Object aplica(Object valor) {
        this.linkAplicar.setConteudo(valor.toString());
        return this.linkAplicar.toString();
    }
}
