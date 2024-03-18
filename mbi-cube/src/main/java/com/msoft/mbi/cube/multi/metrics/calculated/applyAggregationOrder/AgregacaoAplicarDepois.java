package com.msoft.mbi.cube.multi.metrics.calculated.applyAggregationOrder;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculated;

public class AgregacaoAplicarDepois implements AggregationApplyOrder {

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
    public void populateNewValue(MetricCalculated metricCalculated, Double newValue) {
        metricCalculated.setValue(newValue);
    }

    @Override
    public MetricLine getMetricLineUse(MetricLine metricLine, MetricsMap metricsMap) {
        return metricsMap.getMetricLine(metricLine.getDimensionLine(), metricLine.getDimensionColumn());
    }

}
