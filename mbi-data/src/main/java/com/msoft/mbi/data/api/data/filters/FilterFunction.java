package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.indicator.Operator;

public interface FilterFunction {

    int SEQUENCE_FILTER = 1;
    int ACCUMULATED_FILTER = 2;

    boolean verifyCondition(double originalValue);

    void setOperator(Operator operator);

    Operator getOperator();

    void setValue(String valor);

    String getValue();

    String getFormattedValue();

    int getType();

    String getFieldName();

    String getFieldTitle();

    Object clone();
}
