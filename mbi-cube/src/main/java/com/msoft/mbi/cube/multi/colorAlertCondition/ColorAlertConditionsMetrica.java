package com.msoft.mbi.cube.multi.colorAlertCondition;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.generation.CalculoSumarizacaoTipo;
import com.msoft.mbi.cube.multi.generation.CalculoSumarizacaoTipoLinhaMetricaAtual;
import com.msoft.mbi.cube.multi.generation.CalculoSumarizacaoTipoMediaColuna;
import com.msoft.mbi.cube.multi.generation.CalculoSumarizacaoTipoSomaTodasMetricas;
import com.msoft.mbi.cube.multi.generation.CalculoSumarizacaoTipoSomatorio;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

import java.io.Serial;

public abstract class ColorAlertConditionsMetrica extends ColorAlertConditions {

    private MetricaMetaData metaData;
    private CalculoSumarizacaoTipo calculoTipo;

    public ColorAlertConditionsMetrica(int sequencia, ColorAlertProperties propriedadeAlerta, String funcao, int acao, String operador,
                                       MetricaMetaData metaData) {
        super(sequencia, propriedadeAlerta, funcao, acao, operador, metaData.getTipo());
        this.metaData = metaData;
        this.calculoTipo = this.getCalculoTipo(funcao);
    }

    public MetricaMetaData getMetaData() {
        return metaData;
    }

    protected CalculoSumarizacaoTipo getCalculoTipo(String funcao) {
        if (MetricaMetaData.TOTALIZACAO_AH.equals(funcao) || MetricaMetaData.TOTALIZACAO_GERAL.equals(funcao)) {
            return CalculoSumarizacaoTipoSomaTodasMetricas.getInstance();
        } else if (MetricaMetaData.MEDIA_AH.equals(funcao)) {
            return CalculoSumarizacaoTipoMediaColuna.getInstance();
        } else if (MetricaMetaData.VALOR_ACUMULADO_AH.equals(funcao) || MetricaMetaData.TOTALIZACAO_AV.equals(funcao)
                || MetricaMetaData.TOTALIZACAO_PARCIAL.equals(funcao)) {
            return CalculoSumarizacaoTipoSomatorio.getInstance();
        } else {
            return CalculoSumarizacaoTipoLinhaMetricaAtual.getInstance();
        }
    }

    protected Double calculaValor(Dimension dimensionEixoReferencia, Dimension dimension, MetricaMetaData metaData) {
        return this.calculoTipo.calcula(dimensionEixoReferencia, null, dimension, metaData, CalculoSumarizacaoTipo.NORMAL);
    }

    public abstract boolean testaCondicao(Dimension dimensionLinha, Dimension dimensionColuna, Cubo cubo);

    public abstract boolean testaCondicao(Double valor, Dimension dimensionLinha, Dimension dimensionColuna, Cubo cubo);

}
