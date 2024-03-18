package com.msoft.mbi.cube.multi.metrics.aggregation;

import java.io.Serial;
import java.io.Serializable;

public abstract class AgregadorTipo implements Serializable {

    @Serial
    private static final long serialVersionUID = 707089608351906229L;
    protected Double valor;

    public Double getValorAgregado() {
        return this.valor;
    }

    public void setValor(Double novoValor) {
        this.valor = novoValor;
    }

    public abstract void agregaValor(Double novoValor);
}
