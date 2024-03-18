package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class CalculoSumarizacaoTipoLinhaMetricaAtual implements CalculoSumarizacaoTipo {

    private static CalculoSumarizacaoTipoLinhaMetricaAtual calculo = new CalculoSumarizacaoTipoLinhaMetricaAtual();

    private CalculoSumarizacaoTipoLinhaMetricaAtual() {
        super();
    }

    public static CalculoSumarizacaoTipoLinhaMetricaAtual getInstance() {
        return calculo;
    }

    @Override
    public Double calcula(Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, MetricMetaData metaData, String tipoLinha) {
        MetricLine metricLine = dimensionEixoReferencia.getCube().getMetricsMap().getMetricLine(dimensionEixoReferencia, dimension);

        MetricLine metricLineAnterior = null;
        if (dimensionLinhaAnterior != null) {
            metricLineAnterior = dimensionEixoReferencia.getCube().getMetricsMap().getMetricLine(dimensionLinhaAnterior, dimension);
        }
        Metric expressao = metricLine.getMetrics().get(metaData.getTitle());
        Double retorno = expressao.getValor(dimensionEixoReferencia.getCube().getMetricsMap(), metricLine, metricLineAnterior);
        return retorno == null ? 0d : retorno;
    }
}
