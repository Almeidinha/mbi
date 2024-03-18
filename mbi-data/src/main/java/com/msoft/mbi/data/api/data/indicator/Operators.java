package com.msoft.mbi.data.api.data.indicator;

import java.util.Hashtable;

public class Operators extends Hashtable<String, String> {
    public static final String GREATER_THAN = ">";
    public static final String GREATER_TAN_OR_EQUAL = ">=";
    public static final String LESS_THAN = "<";
    public static final String LESS_THAN_OR_EQUAL = "<=";
    public static final String EQUAL_TO = "=";
    public static final String NOT_EQUAL_TO = "<>";
    public static final String CONTAINS = "like";
    public static final String NOT_CONTAINS = "notlike";
    public static final String STARTS_WITH = "like%";
    public static final String FIRST_N = "first(n)";
    public static final String LAST_N = "last(n)";
    public static final String BETWEEN = "between";

    public Operators() {
        super.put(Operators.GREATER_THAN, "greater than");
        super.put(Operators.GREATER_TAN_OR_EQUAL, "greater than or equal to");
        super.put(Operators.LESS_THAN, "less than");
        super.put(Operators.LESS_THAN_OR_EQUAL, "less than or equal to");
        super.put(Operators.EQUAL_TO, "equal to");
        super.put(Operators.NOT_EQUAL_TO, "Not equal to");
        super.put(Operators.CONTAINS, "contains");
        super.put(Operators.NOT_CONTAINS, "not contains");
        super.put(Operators.STARTS_WITH, "starts with");
        super.put(Operators.FIRST_N, "first");
        super.put(Operators.LAST_N, "last");
        super.put(Operators.BETWEEN, "between");
    }

}
