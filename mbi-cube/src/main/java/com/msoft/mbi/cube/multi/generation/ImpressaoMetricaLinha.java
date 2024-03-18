package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public abstract class ImpressaoMetricaLinha {

    protected List<MetricaMetaData> metricas;
    private String funcaoAlertaCelulaMetrica;
    private CalculoSumarizacaoTipo tipoCalculo;

    public ImpressaoMetricaLinha(List<MetricaMetaData> metricas, CalculoSumarizacaoTipo tipoCalculo, String funcaoAlertaCelulaMetrica) {
        this.metricas = metricas;
        this.tipoCalculo = tipoCalculo;
        this.funcaoAlertaCelulaMetrica = funcaoAlertaCelulaMetrica;
    }

    protected List<ColorAlertConditionsMetrica> getAlertasCoresMetrica(MetricaMetaData metaData) {
        return metaData.getAlertasCoresCelula(this.funcaoAlertaCelulaMetrica);
    }

    public List<MetricaMetaData> getMetricas() {
        return metricas;
    }

    public void setMetricas(List<MetricaMetaData> metricas) {
        this.metricas = metricas;
    }

    public String getFuncaoAlertaCelulaMetrica() {
        return funcaoAlertaCelulaMetrica;
    }

    protected void imprimeValorMetrica(MetricaMetaData metaData, String propriedadeCelulaLinha, Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, Impressor impressor, Cubo cubo, String tipoLinha) {
        this.imprimeValorMetrica(metaData, propriedadeCelulaLinha, dimensionEixoReferencia, dimensionLinhaAnterior, dimension, impressor, cubo, this.tipoCalculo, tipoLinha);
    }

    protected void imprimeValorMetrica(MetricaMetaData metaData, String propriedadeCelulaLinha, Dimension dimensionEixoReferencia, Dimension dimensionLinhaAnterior, Dimension dimension, Impressor impressor, Cubo cubo, CalculoSumarizacaoTipo tipoCalculo, String tipoLinha) {
        Double valor = tipoCalculo.calcula(dimensionEixoReferencia, dimensionLinhaAnterior, dimension, metaData, tipoLinha);
        String propriedadeAplicar = propriedadeCelulaLinha;
        String nomeEstilo = dimensionEixoReferencia.searchAlertMetricCell(this.getAlertasCoresMetrica(metaData), valor, dimension);
        if (nomeEstilo != null) {
            propriedadeAplicar = nomeEstilo;
        }
        this.imprimeValorMetrica(metaData, propriedadeAplicar, valor, impressor);
    }

    protected void imprimeValorMetrica(MetricaMetaData metaData, String propriedadeAplicar, Double valor, Impressor impressor) {
        impressor.imprimeValorMetrica(propriedadeAplicar, valor, metaData);
    }

    public abstract void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Impressor impressor, Cubo cubo, String tipoLinha);

}
