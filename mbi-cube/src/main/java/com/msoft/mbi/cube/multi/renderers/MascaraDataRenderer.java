package com.msoft.mbi.cube.multi.renderers;

import java.text.SimpleDateFormat;

public class MascaraDataRenderer implements MascaraRenderer {

    private MascaraRenderer renderer;
    private SimpleDateFormat formatador;

    public MascaraDataRenderer(MascaraRenderer decorator) {
        this(decorator, "dd/MM/yyyy");
    }

    public MascaraDataRenderer(MascaraRenderer decorator, String formato) {
        this.renderer = decorator;
        this.formatador = new SimpleDateFormat(formato);
    }

    @Override
    public Object aplica(Object valor) {
        return this.renderer.aplica(this.formatador.format(valor));
    }
}
