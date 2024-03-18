package com.msoft.mbi.cube.multi.renderers;

import java.io.Serializable;

public class NullValueMask implements Serializable {


    private final String substituteValue;

    public NullValueMask() {
        this.substituteValue = "-";
    }

    public NullValueMask(String valor) {
        this.substituteValue = valor;
    }

    public String getText() {
        return this.substituteValue;
    }
}
