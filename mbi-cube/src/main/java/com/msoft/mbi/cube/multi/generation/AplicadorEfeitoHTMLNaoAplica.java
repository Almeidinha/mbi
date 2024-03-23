package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.renderers.MaskRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MaskLinkHTMLDynamicValueRenderer;

public class AplicadorEfeitoHTMLNaoAplica implements AplicadorEfeitoHTML {

    @Override
    public String aplicaEfeitoHTML(Object valor, MaskRenderer efeitoHTMLDecorator) {
        return valor.toString();
    }

    @Override
    public String aplicaEfeitoHTMLDinamico(Object valorImprimir, String valorParametro, MaskLinkHTMLDynamicValueRenderer efeitoHTMLDecorator) {
        return valorImprimir.toString();
    }
}
