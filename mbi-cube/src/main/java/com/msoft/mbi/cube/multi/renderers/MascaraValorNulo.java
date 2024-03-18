package com.msoft.mbi.cube.multi.renderers;

import java.io.Serializable;

public class MascaraValorNulo implements Serializable {


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
