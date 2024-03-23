package com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculated;

public class AggregationApplyAfter implements AggregationApplyOrder {

    private AggregationApplyAfter() {
        super();
    }

    public static AggregationApplyAfter aggregationApplyAfter;

    public static AggregationApplyAfter getInstance() {
        if (aggregationApplyAfter == null) {
            aggregationApplyAfter = new AggregationApplyAfter();
        }
        return aggregationApplyAfter;
    }

    @Override
    public void populateNewValue(MetricCalculated metricCalculated, Double newValue) {
        metricCalculated.setValue(newValue);
    }

    @Override
    public MetricLine getMetricLineUse(MetricLine metricLine, MetricsMap metricsMap) {
        return metricsMap.getMetricLine(metricLine.getDimensionLine(), metricLine.getDimensionColumn());
    }

}
