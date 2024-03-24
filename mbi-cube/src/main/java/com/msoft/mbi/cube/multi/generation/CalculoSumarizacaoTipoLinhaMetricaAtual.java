package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class CalculoSumarizacaoTipoLinhaMetricaAtual implements CalculationSummaryType {

    private static CalculoSumarizacaoTipoLinhaMetricaAtual calculo = new CalculoSumarizacaoTipoLinhaMetricaAtual();

    private CalculoSumarizacaoTipoLinhaMetricaAtual() {
        super();
    }

    public static CalculoSumarizacaoTipoLinhaMetricaAtual getInstance() {
        return calculo;
    }

    @Override
    public Double calculate(Dimension dimensionReferenceAxis, Dimension previousDimensionLine, Dimension dimension, MetricMetaData metaData, String lineType) {
        MetricLine metricLine = dimensionReferenceAxis.getCube().getMetricsMap().getMetricLine(dimensionReferenceAxis, dimension);

        MetricLine metricLineAnterior = null;
        if (previousDimensionLine != null) {
            metricLineAnterior = dimensionReferenceAxis.getCube().getMetricsMap().getMetricLine(previousDimensionLine, dimension);
        }
        Metric expressao = metricLine.getMetrics().get(metaData.getTitle());
        Double retorno = expressao.getValue(dimensionReferenceAxis.getCube().getMetricsMap(), metricLine, metricLineAnterior);
        return retorno == null ? 0d : retorno;
    }
}
