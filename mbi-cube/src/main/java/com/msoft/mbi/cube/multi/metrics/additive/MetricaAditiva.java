package com.msoft.mbi.cube.multi.metrics.additive;

import java.io.Serial;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizar;

public class MetricaAditiva extends Metrica implements Serializable {

  @Serial
  private static final long serialVersionUID = -1776546115184785420L;

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
  public Double getValor(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior) {
    return this.agregador.getValorAgregado();
  }

  @Override
  public Double calcula(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior) {
    return this.agregador.getValorAgregado();
  }

  @Override
  public Double calcula(MapaMetricas mapaMetricas, LinhaMetrica linhaMetrica, LinhaMetrica linhaMetricaAnterior,  MetricaValorUtilizar nivelCalcular) {
    return null;
  }

  public float getFuncao(String funcao) {
    throw new RuntimeException("Método não implementado ainda");
  }

}
