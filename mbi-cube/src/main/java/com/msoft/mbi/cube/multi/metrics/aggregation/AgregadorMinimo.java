package com.msoft.mbi.cube.multi.metrics.aggregation;

import java.io.Serial;

public class AgregadorMinimo extends AgregadorTipo {

    @Serial
    private static final long serialVersionUID = 2122173781590972169L;

    @Override
    public void agregaValor(Double novoValor) {
        if (this.valor == null) {
            this.valor = novoValor;
        } else {
            if (novoValor < this.valor) {
                this.valor = novoValor;
            }
        }
    }

}
