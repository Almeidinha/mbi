package com.msoft.mbi.cube.multi.renderers;

import java.io.Serial;

public class MascaraDepoisRenderer implements MascaraRenderer {

    @Serial
    private static final long serialVersionUID = -1138299563726748483L;

    private String textoMascara;
    private MascaraRenderer renderer;

    public MascaraDepoisRenderer(MascaraRenderer celulaDecorator, String textoDepois) {
        this.renderer = celulaDecorator;
        this.textoMascara = textoDepois;
    }

    @Override
    public Object aplica(Object valor) {
        return this.renderer.aplica(valor) + this.textoMascara;
    }

}
