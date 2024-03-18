package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class CalculoSumarizacaoTipoSomatorio implements CalculoSumarizacaoTipo {

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
    public Double calcula(Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, MetricMetaData metaData, String tipoLinha) {
        return metaData.calculaValorTotalParcial(dimensionEixoReferencia, dimension);
    }

}
