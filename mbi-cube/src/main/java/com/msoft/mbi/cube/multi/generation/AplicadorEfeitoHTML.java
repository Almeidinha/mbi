package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLValorDinamicoRenderer;

public interface AplicadorEfeitoHTML {

    public String aplicaEfeitoHTML(Object valor, MascaraRenderer efeitoHTMLDecorator);

    public String aplicaEfeitoHTMLDinamico(Object valorImprimir, String valorParametro, MascaraLinkHTMLValorDinamicoRenderer efeitoHTMLDecorator);
}
