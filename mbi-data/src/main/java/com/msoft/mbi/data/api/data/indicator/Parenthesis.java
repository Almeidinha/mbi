package com.msoft.mbi.data.api.data.indicator;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Parenthesis {

    public static final int OPEN = 1;
    public static final int CLOSE = 2;

    private int type;
    private String level ="";

    public Parenthesis() {
        this.type = Parenthesis.OPEN;
    }

    public Parenthesis(int type) {
        this.type = type;
    }

    public Parenthesis(int type, String level) {
        this.type = type;
        this.level = level;
    }

}
