package com.msoft.mbi.cube.multi.generation;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ImpressaoMetricaLinhaTotalizacaoColunasGeral extends ImpressaoMetricaLinha {

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
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Printer printer, Cube cube, String tipoLinha) {
        this.imprimeValorMetrica(null, propriedadeCelula, dimensionLinha, dimensionLinhaAnterior, dimensionColuna, printer, cube, tipoLinha);
    }

    @Override
    protected List<ColorAlertConditionsMetrica> getAlertasCoresMetrica(MetricMetaData metaData) {
        List<ColorAlertConditionsMetrica> alertas = new ArrayList<ColorAlertConditionsMetrica>();
        if (aplicarAlertasCelulaMetrica) {
            for (MetricMetaData metaDataAux : metricas) {
                List<ColorAlertConditionsMetrica> alertasMetrica = metaDataAux.getColorAlertCells(this.getFuncaoAlertaCelulaMetrica());
                if (alertasMetrica != null) {
                    alertas.addAll(metaDataAux.getColorAlertCells(this.getFuncaoAlertaCelulaMetrica()));
                }
            }
        }
        return alertas;
    }

    @Override
    protected void imprimeValorMetrica(MetricMetaData metaData, String propriedadeAplicar, Double valor, Printer printer) {
        int nCasasDecimais = this.getNCasasDecimais();
        printer.printNumberValue(propriedadeAplicar, valor, nCasasDecimais);
    }

    private int getNCasasDecimais() {
        int nCasasDecimais = 0;
        for (MetricMetaData metrica : this.metricas) {
            if (metrica.getDecimalPlacesNumber() > nCasasDecimais) {
                nCasasDecimais = metrica.getDecimalPlacesNumber();
            }
        }
        return nCasasDecimais;
    }
}
