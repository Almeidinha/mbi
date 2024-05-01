package com.msoft.mbi.cube.multi.partialtotalization;

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
        if (dimensionReferenceAxis == null || dimension == null || metricMetaData == null || metricsMap == null) {
            throw new IllegalArgumentException("One or more parameters are null.");
        }

        if (!dimensionReferenceAxis.getDimensionsBelow().isEmpty()) {
            int count = 0;
            double sum = 0.0;
            for (Dimension dimensionBelow : dimensionReferenceAxis.getDimensionsBelow().values()) {
                Double dimensionValue = calculateValue(dimensionBelow, dimension, metricMetaData, metricsMap);
                if (dimensionValue != null) {
                    sum += dimensionValue;
                    count++;
                }
            }
            return (count != 0) ? sum / count : 0.0;
        } else {
            if (!dimension.getDimensionsBelow().isEmpty()) {
                return metricMetaData.calculaValorTotalParcial(dimension, dimensionReferenceAxis);
            } else {
                Dimension dimensionLine = dimensionReferenceAxis.getMetaData().isLine() ? dimensionReferenceAxis : dimension;
                Dimension dimensionColumn = dimensionReferenceAxis.getMetaData().isLine() ? dimension : dimensionReferenceAxis;

                MetricLine metricLine = metricsMap.getMetricLine(dimensionLine, dimensionColumn);
                Metric metricCalculated = metricLine.getMetrics().get(metricMetaData.getTitle());
                return metricCalculated.getValue(metricsMap, metricLine, null);
            }
        }
    }

}
