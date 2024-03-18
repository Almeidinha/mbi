package com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculada;

public class AgregacaoAplicarAntes implements AgregacaoAplicarOrdem {

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
  public void populaNovoValor(MetricaCalculada metricaCalculada, Double novoValor) {
    metricaCalculada.agregaValor(novoValor);
  }

  @Override
  public LinhaMetrica getLinhaMetricaUtilizar(LinhaMetrica linhaMetricaValoresAtuais, MapaMetricas mapaMetricas) {
    return linhaMetricaValoresAtuais;
  }

}
