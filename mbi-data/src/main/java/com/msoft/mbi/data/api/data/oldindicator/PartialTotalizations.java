package com.msoft.mbi.data.api.data.oldindicator;

import java.util.ArrayList;
import java.util.Arrays;

public class PartialTotalizations {

    private ArrayList<PartialTotalization> listaTotalizacoes;

    public PartialTotalizations() {
        this.listaTotalizacoes = new ArrayList<>();
    }

    public void addTotalizacao(PartialTotalization totalizacaoParcial) {
        listaTotalizacoes.add(totalizacaoParcial);
    }

    public void removeTotalizacao(int indice) {
        listaTotalizacoes.remove(indice);
    }

    public void removeTotalizacao(PartialTotalization totalizacaoParcial) {
        listaTotalizacoes.remove(totalizacaoParcial);
    }

    public PartialTotalization getTotalizacaoParcial(Object[][] valores, Field field) {
        PartialTotalization totalizParcial;
        for (PartialTotalization listaTotalizacoe : this.listaTotalizacoes) {
            int tamanho = 0;
            totalizParcial = listaTotalizacoe;
            int valoresTotalizacao = totalizParcial.getValues().length;
            if (valoresTotalizacao == valores.length) {
                for (int x = 0; x < valoresTotalizacao; x++) {
                    if (Arrays.deepEquals(valores[x], totalizParcial.getValues()[x]) && field.equals(totalizParcial.getField())) {
                        tamanho++;
                    }
                }
                if (tamanho == totalizParcial.getValues().length)
                    return totalizParcial;
            }
        }
        return null;
    }

    public double getAcumuladoTotalizParcial(Field field, int sequencia) {
        double soma = 0;
        PartialTotalization totalizParcial;
        for (PartialTotalization listaTotalizacoe : this.listaTotalizacoes) {
            totalizParcial = listaTotalizacoe;
            if (totalizParcial.getField().equals(field) && totalizParcial.getSequence() == sequencia) {
                soma += totalizParcial.getPartialTotalization();
            }
        }
        return soma;
    }
}
