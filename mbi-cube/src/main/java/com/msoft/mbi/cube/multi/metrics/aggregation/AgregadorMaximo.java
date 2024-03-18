package com.msoft.mbi.cube.multi.metrics.aggregation;

public class AgregadorMaximo extends AgregadorTipo {

    private static final long serialVersionUID = -5054572825620129815L;

    @Override
    public void agregaValor(Double novoValor) {
        if (this.valor == null) {
            this.valor = novoValor;
        } else {
            if (novoValor > this.valor) {
                this.valor = novoValor;
            }
        }
    }

}
