package com.msoft.mbi.cube.multi.metrics.additive;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;

public class MetricaAditiva extends Metrica {


  public void add(ResultSet set, String campo) throws SQLException {
    Double valor = (Double) this.getMetaData().getTipo().getValor(set, campo);
    this.add(valor);
  }

  public void add(Double valor) {
    this.agregador.agregaValor(valor);
  }

  @Override
  public String toString() {
    Object o = this.getMetaData().getTipo().format(this.agregador.getValorAgregado());
    return o==null ? "" : o.toString();
  }

  @Override
  public Double getValor(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior) {
    return this.agregador.getValorAgregado();
  }

  @Override
  public Double calcula(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior) {
    return this.agregador.getValorAgregado();
  }

  @Override
  public Double calcula(MapaMetricas mapaMetricas, MetricLine metricLine, MetricLine metricLineAnterior, MetricaValorUtilizar nivelCalcular) {
    return null;
  }

  public float getFuncao(String funcao) {
    throw new RuntimeException("Método não implementado ainda");
  }

}
