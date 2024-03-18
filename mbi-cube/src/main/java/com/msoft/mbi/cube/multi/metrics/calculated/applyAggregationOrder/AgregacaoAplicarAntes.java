package com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculated;

public class AgregacaoAplicarAntes implements AggregationApplyOrder {

  public static AgregacaoAplicarAntes agregacaoAplicarAntes;

  private AgregacaoAplicarAntes() {
    super();
  }

  public static AgregacaoAplicarAntes getInstance() {
    if (agregacaoAplicarAntes == null) {
      agregacaoAplicarAntes = new AgregacaoAplicarAntes();
    }
    return agregacaoAplicarAntes;
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
