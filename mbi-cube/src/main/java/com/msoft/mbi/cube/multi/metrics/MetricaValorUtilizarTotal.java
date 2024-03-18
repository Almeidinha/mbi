package com.msoft.mbi.cube.multi.metrics;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionColunaNula;

public class MetricaValorUtilizarTotal implements MetricaValorUtilizar {

    private static MetricaValorUtilizarTotal instance;
    private List<Dimension> dimensoesColuna;

    public static MetricaValorUtilizarTotal getInstance() {
        if (instance == null) {
            instance = new MetricaValorUtilizarTotal();
        }
        return instance;
    }

    @Override
    public Double getValor(MetricsMap metricsMap, MetricLine metricLine, String tituloCampo) {
        Cube cube = metricLine.getDimensionLine().getCube();
        MetricMetaData metaData = cube.getMetricByTitle(tituloCampo);
        return metaData.calculaValorTotalParcial(metricLine.getDimensionLine(), new DimensionColunaNula(cube));
    }

    @Override
    public Double calculaValor(Metric metric, MetricLine metricLine, MetricsMap metricsMap) {
        return metric.getMetaData().calculaValorTotalParcial(metricLine.getDimensionLine(), metricLine.getDimensionColumn());
    }

    @Override
    public List<Dimension> getDimensoesColunaUtilizar(Cube cube) {
        if (dimensoesColuna == null) {
            dimensoesColuna = new ArrayList<Dimension>();
        }

        return dimensoesColuna;
    }

}
