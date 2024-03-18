package com.msoft.mbi.cube.multi.colorAlertCondition;

import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;

import java.io.Serial;

public abstract class ColorAlertConditionsDimensao extends ColorAlertConditions {

    @Serial
    private static final long serialVersionUID = 1410294793280257481L;

    public ColorAlertConditionsDimensao(int sequencia, ColorAlertProperties propriedadeAlerta, String funcao, int acao, String operador,
                                        DimensaoMetaData metaData) {
        super(sequencia, propriedadeAlerta, funcao, acao, operador, metaData.getTipo());
    }

}
