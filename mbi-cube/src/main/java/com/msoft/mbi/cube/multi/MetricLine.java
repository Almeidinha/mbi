package com.msoft.mbi.cube.multi;

import java.util.Map;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metric;
import lombok.Getter;

@Getter
public class MetricLine {

    private Dimension dimensionLine;
    private Dimension dimensionColumn;
    private Map<String, Metric> metrics;

    public MetricLine(Dimension dimensionLine, Dimension dimensionColumn, Map<String, Metric> metrics) {
        this.dimensionLine = dimensionLine;
        this.dimensionColumn = dimensionColumn;
        this.metrics = metrics;
    }

    public MetricLine getMetricLine(Dimension dimensionLine, Dimension dimensionColumn, Map<String, Metric> metrics) {
        this.dimensionLine = dimensionLine;
        this.dimensionColumn = dimensionColumn;
        this.metrics = metrics;
        return this;
    }

}
