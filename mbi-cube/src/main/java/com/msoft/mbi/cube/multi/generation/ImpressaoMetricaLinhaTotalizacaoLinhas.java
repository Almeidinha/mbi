package com.msoft.mbi.cube.multi.generation;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ImpressaoMetricaLinhaTotalizacaoLinhas extends ImpressaoMetricaLinha {

    private boolean aplicarAlertasCelulaMetrica = true;

    public ImpressaoMetricaLinhaTotalizacaoLinhas(List<MetricMetaData> metricas, List<String> funcoesAtuaisAplicar) {
        super(metricas, CalculoSumarizacaoTipoSomatorio.getInstance(), MetricMetaData.TOTAL_AV);
        this.aplicarAlertasCelulaMetrica = funcoesAtuaisAplicar.contains(MetricMetaData.TOTAL_AV);
    }

    @Override
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Impressor impressor, Cube cube, String tipoLinha) {
        for (MetricMetaData metaData : this.metricas) {
            if (metaData.isTotalLines()) {
                this.imprimeValorMetrica(metaData, propriedadeCelula, dimensionLinha, dimensionLinhaAnterior, dimensionColuna, impressor, cube, tipoLinha);
            } else {
                impressor.imprimeColuna(propriedadeCelula, impressor.getValorVazio());
            }
        }
    }

    @Override
    protected List<ColorAlertConditionsMetrica> getAlertasCoresMetrica(MetricMetaData metaData) {
        List<ColorAlertConditionsMetrica> retorno = new ArrayList<>();
        if (this.aplicarAlertasCelulaMetrica) {
            retorno = super.getAlertasCoresMetrica(metaData);
        }
        return retorno;
    }

}
