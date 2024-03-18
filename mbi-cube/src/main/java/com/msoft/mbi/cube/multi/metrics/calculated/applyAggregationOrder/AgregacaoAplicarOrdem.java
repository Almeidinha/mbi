package com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculada;

public interface AgregacaoAplicarOrdem {

    LinhaMetrica getLinhaMetricaUtilizar(LinhaMetrica linhaMetricaValoresAtuais, MapaMetricas mapaMetricas);

    void populaNovoValor(MetricaCalculada metricaCalculada, Double novoValor);

}
