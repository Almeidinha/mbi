package com.msoft.mbi.cube.multi.coloralertcondition;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class ColorAlertConditionsMetricaValor extends ColorAlertConditionsMetrica {

    protected List<Object> valores;

    public ColorAlertConditionsMetricaValor(int sequencia, ColorAlertProperties propriedadeAlerta, String funcao, int acao, String operador, MetricMetaData metaData, List<Object> valores) {
        super(sequencia, propriedadeAlerta, funcao, acao, operador, metaData);
        this.valores = valores;
    }

    @Override
    public boolean testaCondicao(Dimension dimensionLinha, Dimension dimensionColuna, Cube cube) {
        Double valor = this.calculaValor(dimensionLinha, dimensionColuna, this.getMetaData());
        return this.testaCondicao(valor, dimensionLinha, dimensionColuna, cube);
    }

    @Override
    public boolean testaCondicao(Double valor, Dimension dimensionLinha, Dimension dimensionColuna, Cube cube) {
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
