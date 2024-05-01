package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metadata.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class PrintCurrentMetricLine extends MetricLinePrinter {

    public PrintCurrentMetricLine(List<MetricMetaData> metrics) {
        super(metrics, CalculoSumarizacaoTipoLinhaMetricaAtual.getInstance(), ColorAlertMetadata.NO_FUNCTION);
    }

    @Override
    public void printMetricValues(Dimension dimensionLine, Dimension previousDimensionLine, Dimension dimensionColumn, String cellProperty, Printer printer, Cube cube, String lineType) {
        for (MetricMetaData metaData : this.metricMetaData) {
            this.printMetricValue(metaData, cellProperty, dimensionLine, previousDimensionLine, dimensionColumn, printer, cube, lineType);
        }
    }

}
