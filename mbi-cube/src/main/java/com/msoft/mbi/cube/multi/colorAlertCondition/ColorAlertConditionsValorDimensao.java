package com.msoft.mbi.cube.multi.colorAlertCondition;

import java.io.Serial;
import java.util.List;

import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;

public class ColorAlertConditionsValorDimensao extends ColorAlertConditionsDimensao {

    @Serial
    private static final long serialVersionUID = 6448020742363773654L;

    protected List<Object> valores;

    public ColorAlertConditionsValorDimensao(int sequencia, ColorAlertProperties propriedadeAlerta, String funcao, int acao, String operador,
                                             DimensaoMetaData metaData, List<Object> valores) {
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
