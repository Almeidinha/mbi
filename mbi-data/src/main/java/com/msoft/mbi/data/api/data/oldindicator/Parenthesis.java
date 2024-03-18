package com.msoft.mbi.data.api.data.oldindicator;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Parenthesis {

    public static final int ABRE = 1;
    public static final int FECHA = 2;

    private int type;
    private String nivel="";

    public Parenthesis() {
        this.type = Parenthesis.ABRE;
    }

    public Parenthesis(int type) {
        this.type = type;
    }

    public Parenthesis(int type, String level) {
        this.type = type;
        this.nivel = level;
    }

}
