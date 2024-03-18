package com.msoft.mbi.cube.multi.metrics;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;

public interface MetricaValorUtilizar {

    public Double getValor(MetricsMap metricsMap, MetricLine metricLine, String tituloMetrica);

    public Double calculaValor(Metric expressao, MetricLine metricLine, MetricsMap metricsMap);

    public List<Dimension> getDimensoesColunaUtilizar(Cube cube);
}
