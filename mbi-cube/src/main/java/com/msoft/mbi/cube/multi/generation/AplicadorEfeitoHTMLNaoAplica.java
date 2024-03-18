package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLValorDinamicoRenderer;

public class AplicadorEfeitoHTMLNaoAplica implements AplicadorEfeitoHTML {

    @Override
    public String aplicaEfeitoHTML(Object valor, MascaraRenderer efeitoHTMLDecorator) {
        return valor.toString();
    }

    @Override
    public String aplicaEfeitoHTMLDinamico(Object valorImprimir, String valorParametro, MascaraLinkHTMLValorDinamicoRenderer efeitoHTMLDecorator) {
        return valorImprimir.toString();
    }
}
