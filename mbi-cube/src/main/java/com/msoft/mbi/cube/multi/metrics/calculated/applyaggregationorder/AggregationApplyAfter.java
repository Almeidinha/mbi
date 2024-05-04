package com.msoft.mbi.cube.multi.metrics.calculated.applyaggregationorder;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculated;

public class AggregationApplyAfter implements AggregationApplyOrder {

    private AggregationApplyAfter() {
        super();
    }

    private static class SingletonHolder {
        private static final AggregationApplyAfter INSTANCE = new AggregationApplyAfter();
    }

    public static AggregationApplyAfter getInstance() {
        return AggregationApplyAfter.SingletonHolder.INSTANCE;
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
