package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ImpressaoMetricaLinhaExpressaoParcialLinhas extends MetricLinePrinter {

    public ImpressaoMetricaLinhaExpressaoParcialLinhas(List<MetricMetaData> metricas) {
        super(metricas, CalculoSumarizacaoTipoExpressao.getInstance(), MetricMetaData.EXPRESSION_PARTIAL);
    }

    @Override
    public void printMetricValues(Dimension dimensionLine, Dimension previousDimensionLine, Dimension dimensionColumn, String cellProperty, Printer printer, Cube cube, String lineType) {
        for (MetricMetaData metaData : this.metricMetaData) {
            if (metaData.isExpressionPartialLines()) {
                this.printMetricValue(metaData, cellProperty, dimensionLine, previousDimensionLine, dimensionColumn, printer, cube, lineType);
            } else {
                printer.printColumn(cellProperty, printer.getNullValue());
            }
        }
    }


}
