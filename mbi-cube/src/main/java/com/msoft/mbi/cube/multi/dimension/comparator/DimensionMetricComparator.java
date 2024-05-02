package com.msoft.mbi.cube.multi.dimension.comparator;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricOrdering;

public class DimensionMetricComparator extends DimensionComparator {


    private final MetricsMap metricsMapCube;
    private final List<MetricOrdering> metricOrderings;

    public DimensionMetricComparator(MetricsMap metricsMap, List<MetricOrdering> metricOrderings) {
        this.metricsMapCube = metricsMap;
        this.metricOrderings = metricOrderings;
    }

    private int compareMetrics(MetricLine metricLineOne, MetricLine metricLineTwo, MetricOrdering metricOrdering) {
        Double firstValue = metricOrdering.calculateOrderingValue(this.metricsMapCube, metricLineOne);
        Double secondValue = metricOrdering.calculateOrderingValue(this.metricsMapCube, metricLineTwo);
        int order = metricOrdering.getOrderingType();
        int result;
        if (firstValue == null) {
            if (secondValue == null) {
                result = 0;
            } else {
                result = -1 * order;
            }
        } else if (secondValue == null) {
            result = order;
        } else {
            result = firstValue.compareTo(secondValue) * order;
        }
        return result;
    }

    @Override
    public int compare(Comparable<Dimension> o1, Comparable<Dimension> o2) {
        Dimension dimensionLineOne = (Dimension) o1;
        Dimension dimensionLineTwo = (Dimension) o2;
        int result = 0;

        for (MetricOrdering metricOrdering: this.metricOrderings) {
            MetricLine metricLineOne;
            MetricLine metricLineTwo;
            List<Dimension> dimensionColumns = metricOrdering.getDimensionColumnUse(dimensionLineOne.getCube());

            if (!dimensionColumns.isEmpty()) {
                for (Dimension dimensionColumn: dimensionColumns) {
                    metricLineOne = this.metricsMapCube.getMetricLine(dimensionLineOne, dimensionColumn);
                    metricLineTwo = this.metricsMapCube.getMetricLine(dimensionLineTwo, dimensionColumn);
                    result = this.compareMetrics(metricLineOne, metricLineTwo, metricOrdering);
                    if (result != 0) {
                        break;
                    }
                }
            } else {
                metricLineOne = this.metricsMapCube.getMetricLine(dimensionLineOne);
                metricLineTwo = this.metricsMapCube.getMetricLine(dimensionLineTwo);
                result = this.compareMetrics(metricLineOne, metricLineTwo, metricOrdering);
            }
        }
        if (result == 0) {
            result = dimensionLineOne.getOrderValue().compareTo(dimensionLineTwo.getOrderValue());
        }
        return result;
    }
}
