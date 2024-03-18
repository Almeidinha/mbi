package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class CalculoSumarizacaoTipoLinhaMetricaAtual implements CalculoSumarizacaoTipo {

    private static CalculoSumarizacaoTipoLinhaMetricaAtual calculo = new CalculoSumarizacaoTipoLinhaMetricaAtual();

    private CalculoSumarizacaoTipoLinhaMetricaAtual() {
        super();
    }

    public static CalculoSumarizacaoTipoLinhaMetricaAtual getInstance() {
        return calculo;
    }

    @Override
    public Double calcula(Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, MetricaMetaData metaData, String tipoLinha) {
        LinhaMetrica linhaMetrica = dimensionEixoReferencia.getCube().getMapaMetricas().getLinhaMetrica(dimensionEixoReferencia, dimension);

        LinhaMetrica linhaMetricaAnterior = null;
        if (dimensionLinhaAnterior != null) {
            linhaMetricaAnterior = dimensionEixoReferencia.getCube().getMapaMetricas().getLinhaMetrica(dimensionLinhaAnterior, dimension);
        }
        Metrica expressao = linhaMetrica.getMetricas().get(metaData.getTitulo());
        Double retorno = expressao.getValor(dimensionEixoReferencia.getCube().getMapaMetricas(), linhaMetrica, linhaMetricaAnterior);
        return retorno == null ? 0d : retorno;
    }
}
