package com.msoft.mbi.cube.multi.metrics.aggregation;


public class AgregadorContagem extends AgregadorTipo {


    @Override
    public void agregaValor(Double novoValor) {
        if (this.valor == null) {
            this.valor = (double) 0;
        }
        this.valor++;
    }

}
