package com.msoft.mbi.data.api.data.indicator;

import java.util.HashMap;

public class ArithmeticOperators extends HashMap<Object, Object> {

    public static final String ADDITION = "+";
    public static final String SUBTRACTION = "-";
    public static final String DIVISION = "/";
    public static final String MULTIPLICATION = "*";

    public ArithmeticOperators() {
        super.put(ArithmeticOperators.ADDITION, "mais");
        super.put(ArithmeticOperators.SUBTRACTION, "menos");
        super.put(ArithmeticOperators.DIVISION, "divisão");
        super.put(ArithmeticOperators.MULTIPLICATION, "multiplicação");
    }

}
