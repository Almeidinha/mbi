package com.msoft.mbi.cube.multi.generation;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class PrintMetricLineTotalColumnLines extends MetricLinePrinter {

    private final boolean applyMetricCellAlerts;

    public PrintMetricLineTotalColumnLines(List<MetricMetaData> metrics, CalculationSummaryType calcType, String colorAlertFunction, List<String> currentFunctions) {
        super(metrics, calcType, colorAlertFunction);

        this.applyMetricCellAlerts = currentFunctions.contains(colorAlertFunction);
    }

    @Override
    public void printMetricValues(Dimension dimensionLine, Dimension previousDimensionLine, Dimension dimensionColumn, String cellProperty, Printer printer, Cube cube, String lineType) {
        for (MetricMetaData metaData : this.metricMetaData) {
            this.printMetricValue(metaData, cellProperty, dimensionLine, previousDimensionLine, dimensionColumn, printer, cube, lineType);
        }
    }

    @Override
    protected List<ColorAlertConditionsMetrica> getMetricColorAlerts(MetricMetaData metaData) {
        List<ColorAlertConditionsMetrica> result = new ArrayList<>();
        if (this.applyMetricCellAlerts) {
            result = super.getMetricColorAlerts(metaData);
        }
        return result;
    }

}
