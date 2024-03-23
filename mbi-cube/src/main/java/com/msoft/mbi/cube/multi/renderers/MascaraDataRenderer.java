package com.msoft.mbi.cube.multi.renderers;

import java.text.SimpleDateFormat;

public class MascaraDataRenderer implements MaskRenderer {

    private MaskRenderer renderer;
    private SimpleDateFormat formatador;

    public MascaraDataRenderer(MaskRenderer decorator) {
        this(decorator, "dd/MM/yyyy");
    }

    public MascaraDataRenderer(MaskRenderer decorator, String formato) {
        this.renderer = decorator;
        this.formatador = new SimpleDateFormat(formato);
    }

    @Override
    public Object apply(Object valor) {
        return this.renderer.apply(this.formatador.format(valor));
    }
}
