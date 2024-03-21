package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class CalculoSumarizacaoTipoSomatorio implements CalculationSummaryType {

    private static CalculoSumarizacaoTipoSomatorio tipoCalculo;

    private CalculoSumarizacaoTipoSomatorio() {
        super();
    }

    public static CalculoSumarizacaoTipoSomatorio getInstance() {
        if (tipoCalculo == null) {
            tipoCalculo = new CalculoSumarizacaoTipoSomatorio();
        }
        return tipoCalculo;
    }

    @Override
    public Double calculate(Dimension dimensionReferenceAxis, Dimension previousDimensionLine, Dimension dimension, MetricMetaData metaData, String lineType) {
        return metaData.calculaValorTotalParcial(dimensionReferenceAxis, dimension);
    }

}
