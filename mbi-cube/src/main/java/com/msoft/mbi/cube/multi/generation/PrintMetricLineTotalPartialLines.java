package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class PrintMetricLineTotalPartialLines extends MetricLinePrinter {

    public PrintMetricLineTotalPartialLines(List<MetricMetaData> metrics) {
        super(metrics, CalculoSumarizacaoTipoSomatorio.getInstance(), MetricMetaData.TOTAL_PARTIAL);
    }

    @Override
    public void printMetricValues(Dimension dimensionLine, Dimension previousDimensionLine, Dimension dimensionColumn, String cellProperty, Printer printer, Cube cube, String lineType) {
        for (MetricMetaData metaData : this.metricMetaData) {
            if (metaData.isTotalPartialLines()) {
                this.printMetricValue(metaData, cellProperty, dimensionLine, null, dimensionColumn, printer, cube, lineType);
            } else if (metaData.isPartialTotalExpression()) {
                this.printMetricValue(metaData, cellProperty, dimensionLine, null, dimensionColumn, printer, cube, CalculoSumarizacaoTipoExpressao.getInstance(), lineType);
            } else {
                printer.printColumn(cellProperty, printer.getNullValue());
            }
        }
    }

}
