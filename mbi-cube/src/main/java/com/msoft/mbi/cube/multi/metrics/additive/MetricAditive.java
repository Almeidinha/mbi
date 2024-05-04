package com.msoft.mbi.cube.multi.metrics.additive;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricValueUse;

public class MetricAditive extends Metric {


  public void add(ResultSet set, String campo) throws SQLException {
    Double valor = this.getMetaData().getType().getValue(set, campo);
    this.add(valor);
  }

  public void add(Double valor) {
    this.aggregator.aggregatorValue(valor);
  }

  @Override
  public String toString() {
    Object o = this.getMetaData().getType().format(this.aggregator.getAggregatorValue());
    return o==null ? "" : o.toString();
  }

  @Override
  public Double getValue(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
    return this.aggregator.getAggregatorValue();
  }

  @Override
  public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
    return this.aggregator.getAggregatorValue();
  }

  @Override
  public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior, MetricValueUse calculateLevel) {
    return null;
  }

  public float getFunction(String function) {
    throw new RuntimeException("Método não implementado ainda");
  }

}
