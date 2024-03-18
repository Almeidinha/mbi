package com.msoft.mbi.cube.multi.renderers;

import java.io.Serial;

public class MascaraAntesRenderer implements MascaraRenderer {

    @Serial
    private static final long serialVersionUID = 240470382655275489L;

    private String textoMascara;
    private MascaraRenderer renderer;

    public MascaraAntesRenderer(MascaraRenderer celulaDecorator, String textoMascara) {
        this.renderer = celulaDecorator;
        this.textoMascara = textoMascara;
    }

    public MascaraAntesRenderer(MascaraRenderer celulaDecorator) {
        this.renderer = celulaDecorator;
        this.textoMascara = "R$";
    }

    @Override
    public Object aplica(Object valor) {
        return this.textoMascara + this.renderer.aplica(valor);
    }

}
