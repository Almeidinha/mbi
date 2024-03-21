package com.msoft.mbi.cube.multi.generation;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ImpressaoMetricaLinhaTotalizacaoLinhas extends MetricLinePrinter {

    private boolean aplicarAlertasCelulaMetrica = true;

    public ImpressaoMetricaLinhaTotalizacaoLinhas(List<MetricMetaData> metricas, List<String> funcoesAtuaisAplicar) {
        super(metricas, CalculoSumarizacaoTipoSomatorio.getInstance(), MetricMetaData.TOTAL_AV);
        this.aplicarAlertasCelulaMetrica = funcoesAtuaisAplicar.contains(MetricMetaData.TOTAL_AV);
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
        List<ColorAlertConditionsMetrica> retorno = new ArrayList<>();
        if (this.aplicarAlertasCelulaMetrica) {
            retorno = super.getMetricColorAlerts(metaData);
        }
        return retorno;
    }

}
