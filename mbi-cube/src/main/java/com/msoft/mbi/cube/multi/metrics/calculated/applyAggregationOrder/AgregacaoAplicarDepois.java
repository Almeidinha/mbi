package com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder;

import com.msoft.mbi.cube.multi.LinhaMetrica;
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
    public LinhaMetrica getLinhaMetricaUtilizar(LinhaMetrica linhaMetricaValoresAtuais, MapaMetricas mapaMetricas) {
        return mapaMetricas.getLinhaMetrica(linhaMetricaValoresAtuais.getDimensionLinha(), linhaMetricaValoresAtuais.getDimensionColuna());
    }

}
