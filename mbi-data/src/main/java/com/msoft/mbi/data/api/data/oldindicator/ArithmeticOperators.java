package com.msoft.mbi.data.api.data.oldindicator;

import java.util.HashMap;

public class ArithmeticOperators extends HashMap<Object, Object> {

    public static final String MAIS = "+";
    public static final String MENOS = "-";
    public static final String DIVISAO = "/";
    public static final String MULTIPLICACAO = "*";

    public ArithmeticOperators() {
        super.put(ArithmeticOperators.MAIS, "mais");
        super.put(ArithmeticOperators.MENOS, "menos");
        super.put(ArithmeticOperators.DIVISAO, "divisão");
        super.put(ArithmeticOperators.MULTIPLICACAO, "multiplicação");
    }

}
