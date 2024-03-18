package com.msoft.mbi.cube.multi.renderers.linkHTML;

import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;

public class MascaraLinkHTMLTextoRenderer implements MascaraRenderer {

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
