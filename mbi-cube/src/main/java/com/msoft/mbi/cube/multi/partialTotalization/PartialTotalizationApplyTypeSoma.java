package com.msoft.mbi.cube.multi.partialTotalization;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class PartialTotalizationApplyTypeSoma implements PartialTotalizationApplyType {

    private static PartialTotalizationApplyTypeSoma acumuladoParcialTipo;

    private PartialTotalizationApplyTypeSoma() {
        super();
    }

    public static PartialTotalizationApplyTypeSoma getInstance() {
        if (acumuladoParcialTipo == null) {
            acumuladoParcialTipo = new PartialTotalizationApplyTypeSoma();
        }
        return acumuladoParcialTipo;
    }

    public Double calculateValue(Dimension dimensionReferenceAxis, Dimension dimension, MetricMetaData metricMetaData, MetricsMap metricsMap) {
        Double retorno = (double) 0;
        if (!dimensionReferenceAxis.getDimensionsBelow().isEmpty()) {
            Dimension dimensionAbaixo;
            Double valorDimensao;
            for (Dimension value : dimensionReferenceAxis.getDimensionsBelow().values()) {
                dimensionAbaixo = value;
                valorDimensao = calculateValue(dimensionAbaixo, dimension, metricMetaData, metricsMap);
                retorno += (valorDimensao != null ? valorDimensao : 0);
            }
        } else {
            if (!dimension.getDimensionsBelow().isEmpty()) {
                retorno = metricMetaData.calculaValorTotalParcial(dimension, dimensionReferenceAxis);
            } else {
                Dimension dimensionLinha;
                Dimension dimensionColuna;
                if (dimensionReferenceAxis.getMetaData().isLine()) {
                    dimensionLinha = dimensionReferenceAxis;
                    dimensionColuna = dimension;
                } else {
                    dimensionLinha = dimension;
                    dimensionColuna = dimensionReferenceAxis;
                }

                MetricLine metricLine = metricsMap.getMetricLine(dimensionLinha, dimensionColuna);
                Metric metricCalculada = metricLine.getMetrics().get(metricMetaData.getTitle());
                retorno = metricCalculada.getValor(metricsMap, metricLine, null);
            }
        }
        return retorno;
    }
}
