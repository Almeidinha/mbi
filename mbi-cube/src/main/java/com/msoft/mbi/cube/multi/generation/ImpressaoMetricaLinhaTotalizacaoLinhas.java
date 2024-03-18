package com.msoft.mbi.cube.multi.generation;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class ImpressaoMetricaLinhaTotalizacaoLinhas extends ImpressaoMetricaLinha {

    private boolean aplicarAlertasCelulaMetrica = true;

    public ImpressaoMetricaLinhaTotalizacaoLinhas(List<MetricaMetaData> metricas, List<String> funcoesAtuaisAplicar) {
        super(metricas, CalculoSumarizacaoTipoSomatorio.getInstance(), MetricaMetaData.TOTALIZACAO_AV);
        this.aplicarAlertasCelulaMetrica = funcoesAtuaisAplicar.contains(MetricaMetaData.TOTALIZACAO_AV);
    }

    @Override
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Impressor impressor, Cubo cubo, String tipoLinha) {
        for (MetricaMetaData metaData : this.metricas) {
            if (metaData.isTotalizarLinhas()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, dimensionLinhaAnterior, dimensionColuna, impressor, cubo, tipoLinha);
            } else {
                impressor.imprimeColuna(propriedadeCelula, impressor.getValorVazio());
            }
        }
    }

    @Override
    protected List<ColorAlertConditionsMetrica> getAlertasCoresMetrica(MetricaMetaData metaData) {
        List<ColorAlertConditionsMetrica> retorno = new ArrayList<>();
        if (this.aplicarAlertasCelulaMetrica) {
            retorno = super.getAlertasCoresMetrica(metaData);
        }
        return retorno;
    }

}
