package com.msoft.mbi.cube.multi.generation;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class PrintMetricLineTotalLines extends MetricLinePrinter {

    private final boolean applyCellMetricAlerts;

    public PrintMetricLineTotalLines(List<MetricMetaData> metrics, List<String> functions) {
        super(metrics, CalculoSumarizacaoTipoSomatorio.getInstance(), MetricMetaData.TOTAL_AV);
        this.applyCellMetricAlerts = functions.contains(MetricMetaData.TOTAL_AV);
    }

    @Override
    public void printMetricValues(Dimension dimensionLine, Dimension previousDimensionLine, Dimension dimensionColumn, String cellProperty, Printer printer, Cube cube, String lineType) {
        for (MetricMetaData metaData : this.metricMetaData) {
            if (metaData.isTotalLines()) {
                this.printMetricValue(metaData, cellProperty, dimensionLine, previousDimensionLine, dimensionColumn, printer, cube, lineType);
            } else {
                printer.printColumn(cellProperty, printer.getEmptyValue());
            }
        }
    }

    @Override
    protected List<ColorAlertConditionsMetrica> getMetricColorAlerts(MetricMetaData metaData) {
        List<ColorAlertConditionsMetrica> result = new ArrayList<>();
        if (this.applyCellMetricAlerts) {
            result = super.getMetricColorAlerts(metaData);
        }
        return result;
    }

}
