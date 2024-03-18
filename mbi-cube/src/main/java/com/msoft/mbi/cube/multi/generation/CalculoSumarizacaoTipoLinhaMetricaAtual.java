package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.MetricLine;
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
        MetricLine metricLine = dimensionEixoReferencia.getCube().getMapaMetricas().getMetricLine(dimensionEixoReferencia, dimension);

        MetricLine metricLineAnterior = null;
        if (dimensionLinhaAnterior != null) {
            metricLineAnterior = dimensionEixoReferencia.getCube().getMapaMetricas().getMetricLine(dimensionLinhaAnterior, dimension);
        }
        Metrica expressao = metricLine.getMetrics().get(metaData.getTitulo());
        Double retorno = expressao.getValor(dimensionEixoReferencia.getCube().getMapaMetricas(), metricLine, metricLineAnterior);
        return retorno == null ? 0d : retorno;
    }
}
