package com.msoft.mbi.cube.multi.metrics.aggregation;

import java.io.Serial;

public class AgregadorSoma extends AgregadorTipo {

    @Serial
    private static final long serialVersionUID = -7769974857187182538L;

    @Override
    public void agregaValor(Double novoValor) {
        if (this.valor == null) {
            this.valor = (double) 0;
        }
        this.valor += novoValor;
    }

}
