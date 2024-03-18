package com.msoft.mbi.cube.multi.metrics.aggregation;


public class AgregadorMinimo extends AgregadorTipo {


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
