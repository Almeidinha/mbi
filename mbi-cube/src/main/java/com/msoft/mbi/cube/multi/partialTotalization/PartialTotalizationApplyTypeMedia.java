package com.msoft.mbi.cube.multi.partialTotalization;

import java.util.Iterator;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class PartialTotalizationApplyTypeMedia implements PartialTotalizationApplyType {


    private static PartialTotalizationApplyTypeMedia acumuladoParcialTipo;

    private PartialTotalizationApplyTypeMedia() {
        super();
    }

    public static PartialTotalizationApplyTypeMedia getInstance() {
        if (acumuladoParcialTipo == null) {
            acumuladoParcialTipo = new PartialTotalizationApplyTypeMedia();
        }
        return acumuladoParcialTipo;
    }

    public Double calculateValue(Dimension dimensionReferenceAxis, Dimension dimension, MetricMetaData metricMetaData, MetricsMap metricsMap) {
        Double retorno = (double) 0;
        if (!dimensionReferenceAxis.getDimensionsBelow().isEmpty()) {
            Dimension dimensionAbaixo;
            Double valorDimensao;
            Iterator<Dimension> iDimensoesAbaixo = dimensionReferenceAxis.getDimensionsBelow().values().iterator();
            int count = 0;
            while (iDimensoesAbaixo.hasNext()) {
                dimensionAbaixo = iDimensoesAbaixo.next();
                valorDimensao = calculateValue(dimensionAbaixo, dimension, metricMetaData, metricsMap);
                retorno += (valorDimensao != null ? valorDimensao : 0);
                count++;
            }
            retorno = retorno / count;
        } else {
            if (!dimension.getDimensionsBelow().isEmpty()) {
                retorno = metricMetaData.calculaValorTotalParcial(dimension, dimensionReferenceAxis);
            } else {
                Dimension dimensionLinha = null;
                Dimension dimensionColuna = null;
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
