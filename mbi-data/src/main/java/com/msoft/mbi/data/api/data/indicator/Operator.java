package com.msoft.mbi.data.api.data.indicator;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Operator {

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

    public static Operator copy(Operator template) {
        Operator copy = new Operator();
        copy.symbol = template.symbol;
        copy.description = template.description;

        return copy;
    }

    public Operator copy() {
        Operator copy = new Operator();
        copy.symbol = this.symbol;
        copy.description = this.description;

        return copy;
    }
}
