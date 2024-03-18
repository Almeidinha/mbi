package com.msoft.mbi.cube.multi.metrics;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;

public class MetricaValorUtilizarLinhaMetrica implements MetricaValorUtilizar {

    private static MetricaValorUtilizarLinhaMetrica instance;

    public static MetricaValorUtilizarLinhaMetrica getInstance() {
        if (instance == null) {
            instance = new MetricaValorUtilizarLinhaMetrica();
        }
        return instance;
    }

    @Override
    public Double getValor(MetricsMap metricsMap, MetricLine metricLine, String tituloCampo) {
        Metric expressao = metricLine.getMetrics().get(tituloCampo);
        return expressao.getValor(metricsMap, metricLine, (MetricLine) null);
    }

    @Override
    public Double calculaValor(Metric metric, MetricLine metricLine, MetricsMap metricsMap) {
        return metric.calculate(metricsMap, metricLine, (MetricLine) null);
    }

    @Override
    public List<Dimension> getDimensoesColunaUtilizar(Cube cube) {
        return cube.getDimensionsLastLevelColumns();
    }

}
