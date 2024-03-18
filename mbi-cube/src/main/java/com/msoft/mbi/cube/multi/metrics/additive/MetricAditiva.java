package com.msoft.mbi.cube.multi.metrics.additive;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;

public class MetricAditiva extends Metric {


  public void add(ResultSet set, String campo) throws SQLException {
    Double valor = (Double) this.getMetaData().getType().getValor(set, campo);
    this.add(valor);
  }

  public void add(Double valor) {
    this.aggregator.agregaValor(valor);
  }

  @Override
  public String toString() {
    Object o = this.getMetaData().getType().format(this.aggregator.getValorAgregado());
    return o==null ? "" : o.toString();
  }

  @Override
  public Double getValor(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
    return this.aggregator.getValorAgregado();
  }

  @Override
  public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior) {
    return this.aggregator.getValorAgregado();
  }

  @Override
  public Double calculate(MetricsMap metricsMap, MetricLine metricLine, MetricLine metricLineAnterior, MetricaValorUtilizar calculateLevel) {
    return null;
  }

  public float getFuncao(String funcao) {
    throw new RuntimeException("Método não implementado ainda");
  }

}
