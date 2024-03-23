package com.msoft.mbi.cube.multi.metrics;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;

public interface MetricValueUse {

    public Double getValor(MetricsMap metricsMap, MetricLine metricLine, String title);

    public Double calculateValue(Metric expression, MetricLine metricLine, MetricsMap metricsMap);

    public List<Dimension> getDimensionColumnsUse(Cube cube);
}
