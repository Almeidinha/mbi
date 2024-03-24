package com.msoft.mbi.cube.multi.metrics;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;

public class MetricValueUseLine implements MetricValueUse {

    private static MetricValueUseLine instance;

    public static MetricValueUseLine getInstance() {
        if (instance == null) {
            instance = new MetricValueUseLine();
        }
        return instance;
    }

    @Override
    public Double getValor(MetricsMap metricsMap, MetricLine metricLine, String title) {
        Metric expression = metricLine.getMetrics().get(title);
        return expression.getValue(metricsMap, metricLine, null);
    }

    @Override
    public Double calculateValue(Metric expression, MetricLine metricLine, MetricsMap metricsMap) {
        return expression.calculate(metricsMap, metricLine, null);
    }

    @Override
    public List<Dimension> getDimensionColumnsUse(Cube cube) {
        return cube.getDimensionsLastLevelColumns();
    }

}
