package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.*;

import com.msoft.mbi.cube.multi.calculation.Calculation;

public class CalculationHierarchyOrdering {

    private final List<MetricCalculatedMetaData> metricsCalculated = new ArrayList<>();

    public CalculationHierarchyOrdering(List<MetricCalculatedMetaData> calculatedMetrics) {
        if (calculatedMetrics != null) {
            this.orderCalculationHierarchy(calculatedMetrics);
        }
    }

    private void orderCalculationHierarchy(List<MetricCalculatedMetaData> calculatedMetrics) {

        Map<String, MetricCalculatedMetaData> calculatedMetricsMap = new HashMap<>();
        for (MetricCalculatedMetaData metric : calculatedMetrics) {
            String title = metric.getTitle();
            calculatedMetricsMap.put(title, metric);
        }

        for (MetricCalculatedMetaData metric : calculatedMetrics) {
            this.order(this.metricsCalculated, calculatedMetricsMap, metric);
        }

    }

    private void order(List<MetricCalculatedMetaData> metricList, Map<String, MetricCalculatedMetaData> metricsMap, MetricCalculatedMetaData metric) {
        if (metricList.contains(metric)) {
            return;
        }

        Calculation calculation = metric.createCalculation();
        calculation.getVariables().values().stream()
                .map(metricsMap::get)
                .filter(Objects::nonNull)
                .forEachOrdered(metaData -> this.order(metricList, metricsMap, metaData));

        metricList.add(metric);
    }

    public List<MetricCalculatedMetaData> getCalculatedMetricsOrdered() {
        return metricsCalculated;
    }

}
