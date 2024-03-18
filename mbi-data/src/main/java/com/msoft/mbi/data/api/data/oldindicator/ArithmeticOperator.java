package com.msoft.mbi.data.api.data.oldindicator;

import lombok.Getter;


@Getter
public class ArithmeticOperator implements Cloneable {

    private final String symbol;
    private final String description;

    public ArithmeticOperator(String symbol) {
        this.symbol = symbol;
        this.description = (String) (new ArithmeticOperators()).get(symbol);
    }

    @Override
    public ArithmeticOperator clone() throws CloneNotSupportedException {
        return (ArithmeticOperator) super.clone();
    }
}
