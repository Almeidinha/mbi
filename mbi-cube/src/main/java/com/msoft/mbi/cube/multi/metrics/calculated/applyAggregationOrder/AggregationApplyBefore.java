package com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculated;

public class AggregationApplyBefore implements AggregationApplyOrder {

  public static AggregationApplyBefore aggregationApplyBefore;

  private AggregationApplyBefore() {
    super();
  }

  public static AggregationApplyBefore getInstance() {
    if (aggregationApplyBefore == null) {
      aggregationApplyBefore = new AggregationApplyBefore();
    }
    return aggregationApplyBefore;
  }

  @Override
  public void populateNewValue(MetricCalculated metricCalculated, Double newValue) {
    metricCalculated.aggregateValue(newValue);
  }

  @Override
  public MetricLine getMetricLineUse(MetricLine metricLine, MetricsMap metricsMap) {
    return metricLine;
  }

}
