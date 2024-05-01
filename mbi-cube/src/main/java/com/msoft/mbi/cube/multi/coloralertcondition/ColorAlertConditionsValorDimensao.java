package com.msoft.mbi.cube.multi.coloralertcondition;

import java.util.List;

import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;

public class ColorAlertConditionsValorDimensao extends ColorAlertConditionsDimensao {


    protected List<Object> valores;

    public ColorAlertConditionsValorDimensao(int sequencia, ColorAlertProperties propriedadeAlerta, String funcao, int acao, String operador,
                                             DimensionMetaData metaData, List<Object> valores) {
        super(sequencia, propriedadeAlerta, funcao, acao, operador, metaData);
        this.valores = valores;
    }

    @Override
    public boolean testCondition(Object value) {
        if (this.valores.get(0) != null && value != null) {
            return this.operator.compare(value instanceof String ? value.toString().trim() : value, this.valores);
        } else {
            return (this.valores.get(0) == null && value == null);
        }
    }

}
