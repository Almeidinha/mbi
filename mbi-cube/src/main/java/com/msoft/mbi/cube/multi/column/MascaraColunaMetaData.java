package com.msoft.mbi.cube.multi.column;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MascaraColunaMetaData {

    private String mascara;
    private int tipo;
    public static final int TIPO_ANTES = 1;
    public static final int TIPO_DEPOIS = 2;
    public static final int TIPO_DATA = 3;
    public static final int TIPO_EIS_DIMENSAO_DAT_MES = 4;
    public static final int TIPO_EIS_DIMENSAO_DAT_ANO_MES = 5;
    public static final int TIPO_EIS_DIMENSAO_DAT_PERIODO = 6;
    public static final int TIPO_EIS_DIMENSAO_DAT_SEMANA = 7;

    public MascaraColunaMetaData(String mascara, int tipo) {
        this.mascara = mascara;
        this.tipo = tipo;
    }

}
