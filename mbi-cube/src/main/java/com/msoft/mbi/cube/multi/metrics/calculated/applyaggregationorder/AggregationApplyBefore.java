package com.msoft.mbi.cube.multi.metrics.calculated.applyaggregationorder;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculated;

public class AggregationApplyBefore implements AggregationApplyOrder {


  private AggregationApplyBefore() {
    super();
  }

  private static class SingletonHolder {
    private static final AggregationApplyBefore INSTANCE = new AggregationApplyBefore();
  }
  public static AggregationApplyBefore getInstance() {
    return AggregationApplyBefore.SingletonHolder.INSTANCE;
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
