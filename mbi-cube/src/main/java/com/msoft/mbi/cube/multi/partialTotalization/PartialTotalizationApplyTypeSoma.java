package com.msoft.mbi.cube.multi.partialTotalization;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class PartialTotalizationApplyTypeSoma implements PartialTotalizationApplyType {

    private static PartialTotalizationApplyTypeSoma totalizationApplyTypeSoma;

    private PartialTotalizationApplyTypeSoma() {
        super();
    }

    public static PartialTotalizationApplyTypeSoma getInstance() {
        if (totalizationApplyTypeSoma == null) {
            totalizationApplyTypeSoma = new PartialTotalizationApplyTypeSoma();
        }
        return totalizationApplyTypeSoma;
    }

    public Double calculateValue(Dimension dimensionReferenceAxis, Dimension dimension, MetricMetaData metricMetaData, MetricsMap metricsMap) {
        if (dimensionReferenceAxis == null || dimension == null || metricMetaData == null || metricsMap == null) {
            throw new IllegalArgumentException("Null parameter(s) detected.");
        }

        if (!dimensionReferenceAxis.getDimensionsBelow().isEmpty()) {
            Double result = 0.0;
            for (Dimension dimensionBelow : dimensionReferenceAxis.getDimensionsBelow().values()) {
                result += calculateValue(dimensionBelow, dimension, metricMetaData, metricsMap);
            }
            return result;
        } else {
            if (!dimension.getDimensionsBelow().isEmpty()) {
                return metricMetaData.calculaValorTotalParcial(dimension, dimensionReferenceAxis);
            } else {
                Dimension dimensionLine = dimensionReferenceAxis.getMetaData().isLine() ? dimensionReferenceAxis : dimension;
                Dimension dimensionColumn = dimensionReferenceAxis.getMetaData().isLine() ? dimension : dimensionReferenceAxis;

                MetricLine metricLine = metricsMap.getMetricLine(dimensionLine, dimensionColumn);
                Metric calculatedMetric = metricLine.getMetrics().get(metricMetaData.getTitle());
                return calculatedMetric.getValue(metricsMap, metricLine, null);
            }
        }
    }

}
