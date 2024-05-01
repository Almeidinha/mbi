package com.msoft.mbi.cube.multi.coloralertcondition;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.generation.CalculationSummaryType;
import com.msoft.mbi.cube.multi.generation.CalculoSumarizacaoTipoLinhaMetricaAtual;
import com.msoft.mbi.cube.multi.generation.CalculoSumarizacaoTipoMediaColuna;
import com.msoft.mbi.cube.multi.generation.CalculoSumarizacaoTipoSomaTodasMetricas;
import com.msoft.mbi.cube.multi.generation.CalculoSumarizacaoTipoSomatorio;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public abstract class ColorAlertConditionsMetrica extends ColorAlertConditions {

    private MetricMetaData metaData;
    private CalculationSummaryType calculoTipo;

    public ColorAlertConditionsMetrica(int sequencia, ColorAlertProperties propriedadeAlerta, String funcao, int acao, String operador,
                                       MetricMetaData metaData) {
        super(sequencia, propriedadeAlerta, funcao, acao, operador, metaData.getType());
        this.metaData = metaData;
        this.calculoTipo = this.getCalculoTipo(funcao);
    }

    public MetricMetaData getMetaData() {
        return metaData;
    }

    protected CalculationSummaryType getCalculoTipo(String funcao) {
        if (MetricMetaData.TOTAL_AH.equals(funcao) || MetricMetaData.TOTAL_GENERAL.equals(funcao)) {
            return CalculoSumarizacaoTipoSomaTodasMetricas.getInstance();
        } else if (MetricMetaData.MEDIA_AH.equals(funcao)) {
            return CalculoSumarizacaoTipoMediaColuna.getInstance();
        } else if (MetricMetaData.ACCUMULATED_VALUE_AH.equals(funcao) || MetricMetaData.TOTAL_AV.equals(funcao)
                || MetricMetaData.TOTAL_PARTIAL.equals(funcao)) {
            return CalculoSumarizacaoTipoSomatorio.getInstance();
        } else {
            return CalculoSumarizacaoTipoLinhaMetricaAtual.getInstance();
        }
    }

    protected Double calculaValor(Dimension dimensionEixoReferencia, Dimension dimension, MetricMetaData metaData) {
        return this.calculoTipo.calculate(dimensionEixoReferencia, null, dimension, metaData, CalculationSummaryType.NORMAL);
    }

    public abstract boolean testaCondicao(Dimension dimensionLinha, Dimension dimensionColuna, Cube cube);

    public abstract boolean testaCondicao(Double valor, Dimension dimensionLinha, Dimension dimensionColuna, Cube cube);

}
