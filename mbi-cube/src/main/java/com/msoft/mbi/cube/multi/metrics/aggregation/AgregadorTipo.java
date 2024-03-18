package com.msoft.mbi.cube.multi.metrics.aggregation;


import lombok.Setter;

@Setter
public abstract class AgregadorTipo {

    protected Double valor;

    public Double getValorAgregado() {
        return this.valor;
    }

    public abstract void agregaValor(Double novoValor);
}
