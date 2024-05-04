package com.msoft.mbi.cube.multi.metrics.calculated.applyaggregationorder;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculated;

public interface AggregationApplyOrder {

    MetricLine getMetricLineUse(MetricLine metricLine, MetricsMap metricsMap);

    void populateNewValue(MetricCalculated metricCalculated, Double newValue);

}
