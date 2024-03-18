package com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculada;

public interface AgregacaoAplicarOrdem {

    MetricLine getLinhaMetricaUtilizar(MetricLine metricLineValoresAtuais, MapaMetricas mapaMetricas);

    void populaNovoValor(MetricaCalculada metricaCalculada, Double novoValor);

}
