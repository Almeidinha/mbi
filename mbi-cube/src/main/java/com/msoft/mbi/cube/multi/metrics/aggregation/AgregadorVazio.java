package com.msoft.mbi.cube.multi.metrics.aggregation;


public class AgregadorVazio extends AgregadorTipo {

    @Override
    public void agregaValor(Double novoValor) {
        if (this.valor == null) {
            this.valor = novoValor;
        }
    }

}
