package com.msoft.mbi.cube.multi.renderers;

import java.io.Serial;

public class MascaraPadraoRenderer implements MascaraRenderer {

    @Serial
    private static final long serialVersionUID = -1753638066621097399L;

    @Override
    public Object aplica(Object valor) {
        return valor;
    }
}
