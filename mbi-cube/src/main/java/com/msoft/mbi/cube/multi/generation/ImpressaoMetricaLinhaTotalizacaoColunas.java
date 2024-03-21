package com.msoft.mbi.cube.multi.generation;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ImpressaoMetricaLinhaTotalizacaoColunas extends MetricLinePrinter {

    private boolean aplicarAlertasCelulaMetrica = true;

    public ImpressaoMetricaLinhaTotalizacaoColunas(List<MetricMetaData> metricas, CalculationSummaryType tipoCalculo, String alertaCorCelulaFuncao, List<String> funcoesAtuaisAplicar) {
        super(metricas, tipoCalculo, alertaCorCelulaFuncao);

        this.aplicarAlertasCelulaMetrica = funcoesAtuaisAplicar.contains(alertaCorCelulaFuncao);
    }

    @Override
    public void printMetricValues(Dimension dimensionLine, Dimension previousDimensionLine, Dimension dimensionColumn, String cellProperty, Printer printer, Cube cube, String lineType) {
        for (MetricMetaData metaData : this.metricMetaData) {
            this.printMetricValue(metaData, cellProperty, dimensionLine, previousDimensionLine, dimensionColumn, printer, cube, lineType);
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
