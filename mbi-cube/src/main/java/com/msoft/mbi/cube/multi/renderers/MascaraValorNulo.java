package com.msoft.mbi.cube.multi.renderers;

import java.io.Serial;
import java.io.Serializable;

public class MascaraValorNulo implements Serializable {
    @Serial
    private static final long serialVersionUID = 6881194551850419102L;

    private String valorSubstituto;

    public MascaraValorNulo() {
        this.valorSubstituto = "-";
    }

    public MascaraValorNulo(String valor) {
        this.valorSubstituto = valor;
    }

    public String getTexto() {
        return this.valorSubstituto;
    }
}
