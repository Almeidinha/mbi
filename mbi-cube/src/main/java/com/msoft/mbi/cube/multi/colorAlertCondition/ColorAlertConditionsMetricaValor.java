package com.msoft.mbi.cube.multi.colorAlertCondition;

import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class ColorAlertConditionsMetricaValor extends ColorAlertConditionsMetrica {

    protected List<Object> valores;

    public ColorAlertConditionsMetricaValor(int sequencia, ColorAlertProperties propriedadeAlerta, String funcao, int acao, String operador, MetricaMetaData metaData, List<Object> valores) {
        super(sequencia, propriedadeAlerta, funcao, acao, operador, metaData);
        this.valores = valores;
    }

    @Override
    public boolean testaCondicao(Dimension dimensionLinha, Dimension dimensionColuna, Cubo cubo) {
        Double valor = this.calculaValor(dimensionLinha, dimensionColuna, this.getMetaData());
        return this.testaCondicao(valor, dimensionLinha, dimensionColuna, cubo);
    }

    @Override
    public boolean testaCondicao(Double valor, Dimension dimensionLinha, Dimension dimensionColuna, Cubo cubo) {
        return this.testCondition(valor);
    }

    @Override
    public boolean testCondition(Object value) {
        if (this.valores.get(0) != null && value != null) {
            return this.operator.compare(value, this.valores);
        } else {
            return (this.valores.get(0) == null && value == null);
        }
    }
}
