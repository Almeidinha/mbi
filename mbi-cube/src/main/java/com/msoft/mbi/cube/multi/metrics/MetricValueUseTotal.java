package com.msoft.mbi.cube.multi.metrics;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionNullColumn;

public class MetricValueUseTotal implements MetricValueUse {

    private static MetricValueUseTotal instance;
    private List<Dimension> dimensionColumns;

    public static MetricValueUseTotal getInstance() {
        if (instance == null) {
            instance = new MetricValueUseTotal();
        }
        return instance;
    }

    @Override
    public Double getValor(MetricsMap metricsMap, MetricLine metricLine, String title) {
        Cube cube = metricLine.getDimensionLine().getCube();
        MetricMetaData metaData = cube.getMetricByTitle(title);
        return metaData.calculaValorTotalParcial(metricLine.getDimensionLine(), new DimensionNullColumn(cube));
    }

    @Override
    public Double calculateValue(Metric expression, MetricLine metricLine, MetricsMap metricsMap) {
        return expression.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), metricLine.getDimensionColumn());
    }

    @Override
    public List<Dimension> getDimensionColumnsUse(Cube cube) {
        if (dimensionColumns == null) {
            dimensionColumns = new ArrayList<>();
        }

        return dimensionColumns;
    }

}
