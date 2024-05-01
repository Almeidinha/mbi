package com.msoft.mbi.cube.multi.partialtotalization;

import java.util.Map;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricValueUseTotal;

public class PartialTotalizationApplyTypeExpression implements PartialTotalizationApplyType {


    private static PartialTotalizationApplyTypeExpression accumulatedPartialType;

    private PartialTotalizationApplyTypeExpression() {
        super();
    }

    public static PartialTotalizationApplyTypeExpression getInstance() {
        if (accumulatedPartialType == null) {
            accumulatedPartialType = new PartialTotalizationApplyTypeExpression();
        }
        return accumulatedPartialType;
    }

    @Override
    public Double calculateValue(Dimension dimensionReferenceAxis, Dimension dimension, MetricMetaData metricMetaData, MetricsMap metricsMap) {
        if (!dimensionReferenceAxis.getMetaData().isLine()) {
            Dimension dimAux = dimensionReferenceAxis;
            dimensionReferenceAxis = dimension;
            dimension = dimAux;
        }
        MetricLine metricLine = metricsMap.getMetricLine(dimensionReferenceAxis, dimension);
        Map<String, Metric> metrics = metricLine.getMetrics();
        Metric expression = metrics.get(metricMetaData.getTitle());
        return expression.calculate(metricsMap, metricLine, null, MetricValueUseTotal.getInstance());
    }

}
