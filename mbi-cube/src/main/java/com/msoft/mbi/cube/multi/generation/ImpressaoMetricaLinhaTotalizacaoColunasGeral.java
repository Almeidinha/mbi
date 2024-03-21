package com.msoft.mbi.cube.multi.generation;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ImpressaoMetricaLinhaTotalizacaoColunasGeral extends MetricLinePrinter {

    private static boolean aplicarAlertasCelulaMetrica = true;

    public ImpressaoMetricaLinhaTotalizacaoColunasGeral(List<MetricMetaData> metricas, List<String> funcoesAplicar) {
        super(metricas, CalculoSumarizacaoTipoSomaTodasMetricas.getInstance(), getFuncaoAplicar(funcoesAplicar));
    }

    private static String getFuncaoAplicar(List<String> funcoesAplicar) {
        if (funcoesAplicar.contains(MetricMetaData.TOTAL_GENERAL)) {
            aplicarAlertasCelulaMetrica = true;
            return MetricMetaData.TOTAL_GENERAL;
        } else {
            if (!funcoesAplicar.contains(MetricMetaData.TOTAL_AH)) {
                aplicarAlertasCelulaMetrica = true;
            } else {
                aplicarAlertasCelulaMetrica = false;
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
        List<ColorAlertConditionsMetrica> alertas = new ArrayList<ColorAlertConditionsMetrica>();
        if (aplicarAlertasCelulaMetrica) {
            for (MetricMetaData metaDataAux : metricMetaData) {
                List<ColorAlertConditionsMetrica> alertasMetrica = metaDataAux.getColorAlertCells(this.getMetricCellAlertFunction());
                if (alertasMetrica != null) {
                    alertas.addAll(metaDataAux.getColorAlertCells(this.getMetricCellAlertFunction()));
                }
            }
        }
        return alertas;
    }

    @Override
    protected void printMetricValue(MetricMetaData metaData, String cellProperty, Double valor, Printer printer) {
        int nCasasDecimais = this.getNCasasDecimais();
        printer.printNumberValue(cellProperty, valor, nCasasDecimais);
    }

    private int getNCasasDecimais() {
        int nCasasDecimais = 0;
        for (MetricMetaData metrica : this.metricMetaData) {
            if (metrica.getDecimalPlacesNumber() > nCasasDecimais) {
                nCasasDecimais = metrica.getDecimalPlacesNumber();
            }
        }
        return nCasasDecimais;
    }
}
