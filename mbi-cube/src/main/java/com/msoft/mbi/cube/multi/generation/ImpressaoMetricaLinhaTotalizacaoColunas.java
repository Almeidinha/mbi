package com.msoft.mbi.cube.multi.generation;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class ImpressaoMetricaLinhaTotalizacaoColunas extends ImpressaoMetricaLinha {

    private boolean aplicarAlertasCelulaMetrica = true;

    public ImpressaoMetricaLinhaTotalizacaoColunas(List<MetricaMetaData> metricas, CalculoSumarizacaoTipo tipoCalculo, String alertaCorCelulaFuncao, List<String> funcoesAtuaisAplicar) {
        super(metricas, tipoCalculo, alertaCorCelulaFuncao);

        this.aplicarAlertasCelulaMetrica = funcoesAtuaisAplicar.contains(alertaCorCelulaFuncao);
    }

    @Override
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Impressor impressor, Cubo cubo, String tipoLinha) {
        for (MetricaMetaData metaData : this.metricas) {
            this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, dimensionLinhaAnterior, dimensionColuna, impressor, cubo, tipoLinha);
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
