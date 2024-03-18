package com.msoft.mbi.cube.multi.metrics.aggregation;

import java.io.Serial;

public class AgregadorVazio extends AgregadorTipo {

    @Serial
    private static final long serialVersionUID = 2976550311930945813L;

    @Override
    public void agregaValor(Double novoValor) {
        if (this.valor == null) {
            this.valor = novoValor;
        }
    }

}
