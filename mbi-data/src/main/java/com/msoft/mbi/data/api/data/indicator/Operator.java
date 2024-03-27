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
        switch (symbol.toLowerCase()) {
            case "is":
            case "in(":
            case "==":
                symbol = "=";
                break;
            case "is symbol":
            case "!=":
            case "not in(":
                symbol = "<>";
                break;
            default:
                break;
        }
        this.symbol = symbol;

        this.description = (new Operators()).get(symbol);
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
