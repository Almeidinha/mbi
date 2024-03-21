package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class CalculoSumarizacaoTipoExpressao implements CalculationSummaryType {

    private static CalculoSumarizacaoTipoExpressao calculo;

    private CalculoSumarizacaoTipoExpressao() {
        super();
    }

    public static CalculoSumarizacaoTipoExpressao getInstance() {
        if (calculo == null) {
            calculo = new CalculoSumarizacaoTipoExpressao();
        }
        return calculo;
    }

    public Double calculate(Dimension dimensionReferenceAxis, Dimension previousDimensionLine, Dimension dimension, MetricMetaData metaData, String lineType) {
        Double soma = metaData.calculaValorTotalParcial(dimensionReferenceAxis, dimension);
        return soma;
    }


}
