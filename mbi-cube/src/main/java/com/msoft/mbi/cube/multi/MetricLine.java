package com.msoft.mbi.cube.multi;

import java.util.Map;

import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import lombok.Getter;

@Getter
public class MetricLine {

    private Dimension dimensionLine;
    private Dimension dimensionColumn;
    private Map<String, Metrica> metrics;

    public MetricLine(Dimension dimensionLine, Dimension dimensionColumn, Map<String, Metrica> metrics) {
        this.dimensionLine = dimensionLine;
        this.dimensionColumn = dimensionColumn;
        this.metrics = metrics;
    }

    public MetricLine getMetricLine(Dimension dimensionLine, Dimension dimensionColumn, Map<String, Metrica> metrics) {
        this.dimensionLine = dimensionLine;
        this.dimensionColumn = dimensionColumn;
        this.metrics = metrics;
        return this;
    }

}
