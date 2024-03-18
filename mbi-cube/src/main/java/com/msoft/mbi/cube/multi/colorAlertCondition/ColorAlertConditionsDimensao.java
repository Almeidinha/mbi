package com.msoft.mbi.cube.multi.colorAlertCondition;

import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;

import java.io.Serial;

public abstract class ColorAlertConditionsDimensao extends ColorAlertConditions {

    public ColorAlertConditionsDimensao(int sequencia, ColorAlertProperties propriedadeAlerta, String funcao, int acao, String operador,
                                        DimensaoMetaData metaData) {
        super(sequencia, propriedadeAlerta, funcao, acao, operador, metaData.getTipo());
    }

}
