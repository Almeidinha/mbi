package com.msoft.mbi.data.api.data.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BIMacro {

    private String id = "";
    private String description = "";
    private String fieldType;
    private int incrementalField;

    public static final String HOJE = "@|HOJE|";
    public static final String ONTEM = "@|ONTEM|";
    public static final String ATEHOJE = "@|ATEHOJE|";
    public static final String SEMANAATUAL = "@|SEMANAATUAL|";
    public static final String SEMANAANTERIOR = "@|SEMANAANTERIOR|";
    public static final String MESATUAL = "@|MESATUAL|";
    public static final String ANOATUAL = "@|ANOATUAL|";
    public static final String MESATUALHOJE = "@|MESATUALHOJE|";
    public static final String MESATUALONTEM = "@|MESATUALONTEM|";
    public static final String ANOHOJE = "@|ANOHOJE|";
    public static final String MESANTERIOR = "@|MESANTERIOR|";
    public static final String ANOANTERIOR = "@|ANOANTERIOR|";
    public static final String ULT15DIAS = "@|ULT15DIAS|";
    public static final String ULT30DIAS = "@|ULT30DIAS|";
    public static final String ULT3MESES = "@|ULT3MESES|";
    public static final String ULT6MESES = "@|ULT6MESES|";
    public static final String ULT12MESES = "@|ULT12MESES|";
    public static final String ULT13MESES = "@|ULT13MESES|";
    public static final String PROX15DIAS = "@|PROX15DIAS|";
    public static final String PROX30DIAS = "@|PROX30DIAS|";
    public static final String PROX3MESES = "@|PROX3MESES|";
    public static final String PROX6MESES = "@|PROX6MESES|";
    public static final String PROX12MESES = "@|PROX12MESES|";

    public static final String LOGIN_USUARIO_LOGADO = "@|LOGIN_USUARIO_LOGADO|";
    public static final String NOME_USUARIO_LOGADO = "@|NOME_USUARIO_LOGADO|";
    public static final String EMAIL_USUARIO_LOGADO = "@|EMAIL_USUARIO_LOGADO|";

    public BIMacro() {
    }

    public void setID(String id) {
        if (id.charAt(0) == '\'') {
            id = id.substring(1, id.length() - 1);
        }
        this.id = id;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BIMacro m = (BIMacro) super.clone();
        m.setIncrementalField(this.incrementalField);
        m.setDescription(this.description);
        m.setID(this.id);
        m.setFieldType(this.fieldType);
        return m;
    }

}
