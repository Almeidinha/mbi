package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class CalculoSumarizacaoTipoMediaLinha implements CalculoSumarizacaoTipo {

    private static CalculoSumarizacaoTipoMediaLinha calculo;

    private CalculoSumarizacaoTipoMediaLinha() {
        super();
    }

    public static CalculoSumarizacaoTipoMediaLinha getInstance() {
        if (calculo == null) {
            calculo = new CalculoSumarizacaoTipoMediaLinha();
        }
        return calculo;
    }

    @Override
    public Double calcula(Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, MetricMetaData metaData, String tipoLinha) {
        Double soma = metaData.calculaValorTotalParcial(dimensionEixoReferencia, dimension);
        int qtdDimensoes = dimensionEixoReferencia.getTotalSize();
        return soma / (qtdDimensoes != 0 ? qtdDimensoes : 1);
    }

}
