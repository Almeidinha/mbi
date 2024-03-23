package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.renderers.MaskRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MaskLinkHTMLDynamicValueRenderer;

public interface AplicadorEfeitoHTML {

    public String aplicaEfeitoHTML(Object valor, MaskRenderer efeitoHTMLDecorator);

    public String aplicaEfeitoHTMLDinamico(Object valorImprimir, String valorParametro, MaskLinkHTMLDynamicValueRenderer efeitoHTMLDecorator);
}
