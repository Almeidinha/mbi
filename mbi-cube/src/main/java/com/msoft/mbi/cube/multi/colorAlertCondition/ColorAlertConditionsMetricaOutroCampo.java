package com.msoft.mbi.cube.multi.colorAlertCondition;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.generation.CalculoSumarizacaoTipo;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class ColorAlertConditionsMetricaOutroCampo extends ColorAlertConditionsMetrica {

    @Serial
    private static final long serialVersionUID = -1376954315387809081L;

    private TipoComparacaoOutroCampo tipoComparacao;
    private Double valorReferencia;
    private MetricaMetaData outroCampo;
    private CalculoSumarizacaoTipo calculoOutroCampo;
    private String funcaoOutroCampo;

    public ColorAlertConditionsMetricaOutroCampo(int sequencia, ColorAlertProperties propriedadeAlerta, String funcao, int acao, String operador,
                                                 MetricaMetaData metaData, String tipoComparacao, Double valorReferencia, MetricaMetaData outroCampo, String funcaoOutroCampo) {
        super(sequencia, propriedadeAlerta, funcao, acao, operador, metaData);
        this.setTipoComparacao(tipoComparacao);
        this.valorReferencia = valorReferencia;
        this.outroCampo = outroCampo;
        this.calculoOutroCampo = this.getCalculoTipo(funcaoOutroCampo);
        this.funcaoOutroCampo = funcaoOutroCampo;
    }

    public void setTipoComparacao(String tipoComparacao) {
        if (AlertaCorMetaData.TIPO_COMPARACAO_PERCENTUAL.equals(tipoComparacao)) {
            this.tipoComparacao = TipoComparacaoOutroCampoPercentual.getInstance();
        } else {
            this.tipoComparacao = TipoComparacaoOutroCampoValor.getInstance();
        }
    }

    public boolean testaCondicao(Dimension dimensionLinha, Dimension dimensionColuna, Cubo cubo) {
        Double valorMetrica = this.calculaValor(dimensionLinha, dimensionColuna, this.getMetaData());
        return this.testaCondicao(valorMetrica, dimensionLinha, dimensionColuna, cubo);
    }

    @Override
    public boolean testaCondicao(Double valor, Dimension dimensionLinha, Dimension dimensionColuna, Cubo cubo) {

        if (MetricaMetaData.TOTALIZACAO_AV.equals(this.funcaoOutroCampo)) {
            Dimension dim = dimensionLinha;
            while (!dim.isFirstDimensionColumnSameLevel()) {
                dim = dim.getParent();
            }

            dimensionLinha = dim;
        }

        if (MetricaMetaData.TOTALIZACAO_PARCIAL.equals(this.funcaoOutroCampo)) {
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
