package com.msoft.mbi.cube.multi.metrics;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class MetricOrdering implements Comparable<MetricOrdering> {

    @Getter
    private final int orderingType;
    @Getter
    private final Integer orderingSequence;
    @Getter
    private final String metricTitle;
    private final MetricValueUse metricValue;

    public MetricOrdering(String orderDirection, int orderingSequence, String metricTitle, MetricValueUse metricValueUse) {
        this.orderingType = "ASC".equals(orderDirection) ? 1 : -1;
        this.orderingSequence = orderingSequence;
        this.metricTitle = metricTitle;
        this.metricValue = metricValueUse;
    }

    @Override
    public int compareTo(MetricOrdering o) {
        return this.orderingSequence.compareTo(o.getOrderingSequence());
    }

    public Double calculateOrderingValue(MetricsMap metricsMap, MetricLine metricLine) {
        return this.metricValue.getValor(metricsMap, metricLine, this.getMetricTitle());
    }

    public List<Dimension> getDimensionColumnUse(Cube cube) {
        return this.metricValue.getDimensionColumnsUse(cube);
    }
}
