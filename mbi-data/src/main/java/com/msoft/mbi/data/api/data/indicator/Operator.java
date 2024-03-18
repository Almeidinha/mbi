package com.msoft.mbi.data.api.data.indicator;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Operator implements Cloneable {

    private String symbol;
    private String description;

    public Operator(String symbol) {
        if (symbol.equalsIgnoreCase("IS") || symbol.equalsIgnoreCase("IN(")) {
            symbol = "=";
        } else if (symbol.equalsIgnoreCase("IS symbol") || symbol.equalsIgnoreCase("!=")
                || symbol.equalsIgnoreCase("NOT IN(")) {
            symbol = "<>";
        } else if (symbol.equalsIgnoreCase("==")) {
            symbol = "=";
        }
        this.symbol = symbol;
        Operators op = new Operators();
        this.description = op.get(symbol);
    }

    @Override
    public Operator clone() {
        try {
            return (Operator) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
