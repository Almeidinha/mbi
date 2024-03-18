package com.msoft.mbi.cube.multi.metrics.aggregation;

import java.io.Serial;

public class AgregadorMedia extends AgregadorTipo {

    @Serial
    private static final long serialVersionUID = -6502777854139941241L;

    private Double soma;
    private int qtdValores = 0;

    @Override
    public void agregaValor(Double novoValor) {
        if (this.soma == null) {
            this.soma = (double) 0;
        }
        this.soma += novoValor;
        this.qtdValores++;
        this.valor = this.soma / this.qtdValores;
    }

}
