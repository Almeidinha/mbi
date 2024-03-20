package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public abstract class ImpressaoMetricaLinha {

    protected List<MetricMetaData> metricas;
    private String funcaoAlertaCelulaMetrica;
    private CalculoSumarizacaoTipo tipoCalculo;

    public ImpressaoMetricaLinha(List<MetricMetaData> metricas, CalculoSumarizacaoTipo tipoCalculo, String funcaoAlertaCelulaMetrica) {
        this.metricas = metricas;
        this.tipoCalculo = tipoCalculo;
        this.funcaoAlertaCelulaMetrica = funcaoAlertaCelulaMetrica;
    }

    protected List<ColorAlertConditionsMetrica> getAlertasCoresMetrica(MetricMetaData metaData) {
        return metaData.getColorAlertCells(this.funcaoAlertaCelulaMetrica);
    }

    public List<MetricMetaData> getMetricas() {
        return metricas;
    }

    public void setMetricas(List<MetricMetaData> metricas) {
        this.metricas = metricas;
    }

    public String getFuncaoAlertaCelulaMetrica() {
        return funcaoAlertaCelulaMetrica;
    }

    protected void imprimeValorMetrica(MetricMetaData metaData, String propriedadeCelulaLinha, Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, Printer printer, Cube cube, String tipoLinha) {
        this.imprimeValorMetrica(metaData, propriedadeCelulaLinha, dimensionEixoReferencia, dimensionLinhaAnterior, dimension, printer, cube, this.tipoCalculo, tipoLinha);
    }

    protected void imprimeValorMetrica(MetricMetaData metaData, String propriedadeCelulaLinha, Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, Printer printer, Cube cube, CalculoSumarizacaoTipo tipoCalculo, String tipoLinha) {
        Double valor = tipoCalculo.calcula(dimensionEixoReferencia, dimensionLinhaAnterior, dimension, metaData, tipoLinha);
        String propriedadeAplicar = propriedadeCelulaLinha;
        String nomeEstilo = dimensionEixoReferencia.searchAlertMetricCell(this.getAlertasCoresMetrica(metaData), valor, dimension);
        if (nomeEstilo != null) {
            propriedadeAplicar = nomeEstilo;
        }
        this.imprimeValorMetrica(metaData, propriedadeAplicar, valor, printer);
    }

    protected void imprimeValorMetrica(MetricMetaData metaData, String propriedadeAplicar, Double valor, Printer printer) {
        printer.printMetricValue(propriedadeAplicar, valor, metaData);
    }

    public abstract void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Printer printer, Cube cube, String tipoLinha);

}
