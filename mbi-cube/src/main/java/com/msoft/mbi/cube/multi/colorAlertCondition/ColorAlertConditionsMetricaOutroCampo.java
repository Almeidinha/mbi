package com.msoft.mbi.cube.multi.colorAlertCondition;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.generation.CalculoSumarizacaoTipo;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ColorAlertConditionsMetricaOutroCampo extends ColorAlertConditionsMetrica {


    private TipoComparacaoOutroCampo tipoComparacao;
    private Double valorReferencia;
    private MetricMetaData outroCampo;
    private CalculoSumarizacaoTipo calculoOutroCampo;
    private String funcaoOutroCampo;

    public ColorAlertConditionsMetricaOutroCampo(int sequencia, ColorAlertProperties propriedadeAlerta, String funcao, int acao, String operador,
                                                 MetricMetaData metaData, String tipoComparacao, Double valorReferencia, MetricMetaData outroCampo, String funcaoOutroCampo) {
        super(sequencia, propriedadeAlerta, funcao, acao, operador, metaData);
        this.setTipoComparacao(tipoComparacao);
        this.valorReferencia = valorReferencia;
        this.outroCampo = outroCampo;
        this.calculoOutroCampo = this.getCalculoTipo(funcaoOutroCampo);
        this.funcaoOutroCampo = funcaoOutroCampo;
    }

    public void setTipoComparacao(String tipoComparacao) {
        if (ColorAlertMetadata.PERCENT_COMPARISON_ALERT_TYPE.equals(tipoComparacao)) {
            this.tipoComparacao = TipoComparacaoOutroCampoPercentual.getInstance();
        } else {
            this.tipoComparacao = TipoComparacaoOutroCampoValor.getInstance();
        }
    }

    public boolean testaCondicao(Dimension dimensionLinha, Dimension dimensionColuna, Cube cube) {
        Double valorMetrica = this.calculaValor(dimensionLinha, dimensionColuna, this.getMetaData());
        return this.testaCondicao(valorMetrica, dimensionLinha, dimensionColuna, cube);
    }

    @Override
    public boolean testaCondicao(Double valor, Dimension dimensionLinha, Dimension dimensionColuna, Cube cube) {

        if (MetricMetaData.TOTAL_AV.equals(this.funcaoOutroCampo)) {
            Dimension dim = dimensionLinha;
            while (!dim.isFirstDimensionColumnSameLevel()) {
                dim = dim.getParent();
            }

            dimensionLinha = dim;
        }

        if (MetricMetaData.TOTAL_PARTIAL.equals(this.funcaoOutroCampo)) {
            dimensionLinha = dimensionLinha.getParent();
        }

        Double valorOutroCampo = this.calculoOutroCampo.calcula(dimensionLinha, null, dimensionColuna, this.outroCampo, CalculoSumarizacaoTipo.NORMAL);

        if (valorOutroCampo != null) {
            valorOutroCampo = this.tipoComparacao.getValorComparar(valorOutroCampo, this.valorReferencia);
            List<Object> valores = new ArrayList<Object>();
            valores.add(valorOutroCampo);
            return this.operator.compare(valor, valores);
        } else {
            return (valor == null);
        }
    }

    @Override
    public boolean testCondition(Object value) {
        return false;
    }

}
