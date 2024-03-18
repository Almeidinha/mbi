package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class CalculoSumarizacaoTipoExpressao implements CalculoSumarizacaoTipo {

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

    public Double calcula(Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, MetricaMetaData metaData, String tipoLinha) {
        Double soma = metaData.calculaValorTotalParcial(dimensionEixoReferencia, dimension);
        return soma;
    }


}
