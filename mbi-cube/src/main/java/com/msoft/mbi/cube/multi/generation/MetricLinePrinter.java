package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import lombok.Getter;
import lombok.Setter;

public abstract class MetricLinePrinter {

    @Setter
    @Getter
    protected List<MetricMetaData> metricMetaData;
    @Getter
    private final String metricCellAlertFunction;
    private final CalculationSummaryType calcType;

    public MetricLinePrinter(List<MetricMetaData> metricMetaData, CalculationSummaryType calcType, String metricCellAlertFunction) {
        this.metricMetaData = metricMetaData;
        this.calcType = calcType;
        this.metricCellAlertFunction = metricCellAlertFunction;
    }

    protected List<ColorAlertConditionsMetrica> getMetricColorAlerts(MetricMetaData metaData) {
        return metaData.getColorAlertCells(this.metricCellAlertFunction);
    }

    protected void printMetricValue(MetricMetaData metaData, String lineCellProperty, Dimension dimensionReferenceAxis, Dimension previousDimensionLine, Dimension dimension, Printer printer, Cube cube, String lineType) {
        this.printMetricValue(metaData, lineCellProperty, dimensionReferenceAxis, previousDimensionLine, dimension, printer, cube, this.calcType, lineType);
    }

    protected void printMetricValue(MetricMetaData metaData, String lineCellProperty, Dimension dimensionReferenceAxis, Dimension previousDimensionLine, Dimension dimension, Printer printer, Cube cube, CalculationSummaryType calcType, String lineType) {
        Double valor = calcType.calculate(dimensionReferenceAxis, previousDimensionLine, dimension, metaData, lineType);
        String propriedadeAplicar = lineCellProperty;
        String styleName = dimensionReferenceAxis.searchAlertMetricCell(this.getMetricColorAlerts(metaData), valor, dimension);
        if (styleName != null) {
            propriedadeAplicar = styleName;
        }
        this.printMetricValue(metaData, propriedadeAplicar, valor, printer);
    }

    protected void printMetricValue(MetricMetaData metaData, String cellProperty, Double valor, Printer printer) {
        printer.printMetricValue(cellProperty, valor, metaData);
    }

    public abstract void printMetricValues(Dimension dimensionLine, Dimension previousDimensionLine, Dimension dimensionColumn, String cellProperty, Printer printer, Cube cube, String lineType);

}
