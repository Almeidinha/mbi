package com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculada;

public class AgregacaoAplicarDepois implements AgregacaoAplicarOrdem {

    private AgregacaoAplicarDepois() {
        super();
    }

    public static AgregacaoAplicarDepois agregacaoAplicarDepois;

    public static AgregacaoAplicarDepois getInstance() {
        if (agregacaoAplicarDepois == null) {
            agregacaoAplicarDepois = new AgregacaoAplicarDepois();
        }
        return agregacaoAplicarDepois;
    }

    @Override
    public void populaNovoValor(MetricaCalculada metricaCalculada, Double novoValor) {
        metricaCalculada.setValor(novoValor);
    }

    @Override
    public MetricLine getLinhaMetricaUtilizar(MetricLine metricLineValoresAtuais, MapaMetricas mapaMetricas) {
        return mapaMetricas.getMetricLine(metricLineValoresAtuais.getDimensionLine(), metricLineValoresAtuais.getDimensionColumn());
    }

}
