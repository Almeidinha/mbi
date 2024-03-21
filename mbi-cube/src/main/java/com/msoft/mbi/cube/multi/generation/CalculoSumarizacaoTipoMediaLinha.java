package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class CalculoSumarizacaoTipoMediaLinha implements CalculationSummaryType {

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
    public Double calculate(Dimension dimensionReferenceAxis, Dimension previousDimensionLine, Dimension dimension, MetricMetaData metaData, String lineType) {
        Double soma = metaData.calculaValorTotalParcial(dimensionReferenceAxis, dimension);
        int qtdDimensoes = dimensionReferenceAxis.getTotalSize();
        return soma / (qtdDimensoes != 0 ? qtdDimensoes : 1);
    }

}
