package com.msoft.mbi.cube.multi.generation;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class PrintMetricLineTotalGeneralColumnLines extends MetricLinePrinter {

    private static boolean applyCellMetricAlerts = true;

    public PrintMetricLineTotalGeneralColumnLines(List<MetricMetaData> metrics, List<String> functions) {
        super(metrics, CalculoSumarizacaoTipoSomaTodasMetricas.getInstance(), getApplyFunction(functions));
    }

    private static String getApplyFunction(List<String> functions) {
        if (functions.contains(MetricMetaData.TOTAL_GENERAL)) {
            applyCellMetricAlerts = true;
            return MetricMetaData.TOTAL_GENERAL;
        } else {
            if (!functions.contains(MetricMetaData.TOTAL_AH)) {
                applyCellMetricAlerts = true;
            } else {
                applyCellMetricAlerts = false;
            }
            return MetricMetaData.TOTAL_AH;
        }
    }


    @Override
    public void printMetricValues(Dimension dimensionLine, Dimension previousDimensionLine, Dimension dimensionColumn, String cellProperty, Printer printer, Cube cube, String lineType) {
        this.printMetricValue(null, cellProperty, dimensionLine, previousDimensionLine, dimensionColumn, printer, cube, lineType);
    }

    @Override
    protected List<ColorAlertConditionsMetrica> getMetricColorAlerts(MetricMetaData metaData) {
        List<ColorAlertConditionsMetrica> alerts = new ArrayList<>();
        if (applyCellMetricAlerts) {
            for (MetricMetaData metaDataAux : metricMetaData) {
                List<ColorAlertConditionsMetrica> metricAlerts = metaDataAux.getColorAlertCells(this.getMetricCellAlertFunction());
                if (metricAlerts != null) {
                    alerts.addAll(metaDataAux.getColorAlertCells(this.getMetricCellAlertFunction()));
                }
            }
        }
        return alerts;
    }

    @Override
    protected void printMetricValue(MetricMetaData metaData, String cellProperty, Double valor, Printer printer) {
        int decimalPlaces = this.getDecimalPlace();
        printer.printNumberValue(cellProperty, valor, decimalPlaces);
    }

    private int getDecimalPlace() {
        int maxDecimalPlaces = 0;
        for (MetricMetaData metric : this.metricMetaData) {
            maxDecimalPlaces = Math.max(maxDecimalPlaces, metric.getDecimalPlacesNumber());
        }
        return maxDecimalPlaces;
    }
}
