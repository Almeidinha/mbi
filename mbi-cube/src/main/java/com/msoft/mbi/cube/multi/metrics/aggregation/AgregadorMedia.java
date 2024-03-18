package com.msoft.mbi.cube.multi.metrics.aggregation;

public class AgregadorMedia extends AgregadorTipo {


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
