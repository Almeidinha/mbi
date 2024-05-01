package com.msoft.mbi.cube.multi.coloralertcondition;

import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;

public abstract class ColorAlertConditionsDimensao extends ColorAlertConditions {

    public ColorAlertConditionsDimensao(int sequencia, ColorAlertProperties propriedadeAlerta, String funcao, int acao, String operador,
                                        DimensionMetaData metaData) {
        super(sequencia, propriedadeAlerta, funcao, acao, operador, metaData.getDataType());
    }

}
