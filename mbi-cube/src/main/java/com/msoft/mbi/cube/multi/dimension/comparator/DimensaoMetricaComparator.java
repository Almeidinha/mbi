package com.msoft.mbi.cube.multi.dimension.comparator;

import java.util.Iterator;
import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricOrdering;

public class DimensaoMetricaComparator extends DimensaoComparator {


    private MetricsMap metricsMapCubo;
    private List<MetricOrdering> ordenacoesMetrica;

    public DimensaoMetricaComparator(MetricsMap metricsMap, List<Dimension> dimensoesColuna, List<MetricOrdering> metricasOrdenadas) {
        this.metricsMapCubo = metricsMap;
        this.ordenacoesMetrica = metricasOrdenadas;
    }

    private int comparaMetricas(int retorno, MetricLine metricLineDimensao1, MetricLine metricLineDimensao2, MetricOrdering metricOrdering) {
        Double valorDimensao1 = metricOrdering.calculateOrderingValue(this.metricsMapCubo, metricLineDimensao1);
        Double valorDimensao2 = metricOrdering.calculateOrderingValue(this.metricsMapCubo, metricLineDimensao2);
        int ordem = metricOrdering.getOrderingType();
        if (valorDimensao1 == null) {
            if (valorDimensao2 == null) {
                retorno = 0;
            } else {
                retorno = -1 * ordem;
            }
        } else if (valorDimensao2 == null) {
            retorno = ordem;
        } else {
            retorno = valorDimensao1.compareTo(valorDimensao2) * ordem;
        }
        return retorno;
    }

    @Override
    public int compare(Comparable<Dimension> o1, Comparable<Dimension> o2) {
        Dimension dimensionLinha1 = (Dimension) o1;
        Dimension dimensionLinha2 = (Dimension) o2;
        int retorno = 0;
        Iterator<MetricOrdering> iMetricasOrdenadas = this.ordenacoesMetrica.iterator();
        while (retorno == 0 && iMetricasOrdenadas.hasNext()) {
            MetricOrdering metricOrdering = iMetricasOrdenadas.next();
            MetricLine metricLineDimensao1 = null;
            MetricLine metricLineDimensao2 = null;
            List<Dimension> lDimensoesColuna = metricOrdering.getDimensionColumnUse(dimensionLinha1.getCube());
            Iterator<Dimension> iDimensoesColuna = lDimensoesColuna.iterator();
            if (lDimensoesColuna.size() > 0) {
                while (retorno == 0 && iDimensoesColuna.hasNext()) {
                    Dimension dimensionColuna = iDimensoesColuna.next();
                    metricLineDimensao1 = this.metricsMapCubo.getMetricLine(dimensionLinha1, dimensionColuna);
                    metricLineDimensao2 = this.metricsMapCubo.getMetricLine(dimensionLinha2, dimensionColuna);
                    retorno = this.comparaMetricas(retorno, metricLineDimensao1, metricLineDimensao2, metricOrdering);
                }
            } else {
                metricLineDimensao1 = this.metricsMapCubo.getMetricLine(dimensionLinha1);
                metricLineDimensao2 = this.metricsMapCubo.getMetricLine(dimensionLinha2);
                retorno = this.comparaMetricas(retorno, metricLineDimensao1, metricLineDimensao2, metricOrdering);
            }
        }
        if (retorno == 0) {
            retorno = dimensionLinha1.getOrderValue().compareTo(dimensionLinha2.getOrderValue());
        }
        return retorno;
    }
}
