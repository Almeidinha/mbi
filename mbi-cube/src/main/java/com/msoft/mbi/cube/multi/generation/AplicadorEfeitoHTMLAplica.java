package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.renderers.MaskRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MaskLinkHTMLDynamicValueRenderer;

public class AplicadorEfeitoHTMLAplica implements AplicadorEfeitoHTML {

    @Override
    public String aplicaEfeitoHTML(Object valor, MaskRenderer efeitoHTMLDecorator) {
        return efeitoHTMLDecorator.apply(valor).toString();
    }

    @Override
    public String aplicaEfeitoHTMLDinamico(Object valorImprimir, String valorParametro, MaskLinkHTMLDynamicValueRenderer efeitoHTMLDecorator) {
        return efeitoHTMLDecorator.apply(valorImprimir, valorParametro).toString();
    }
}
