package com.msoft.mbi.data.api.data.oldfilters;

import com.msoft.mbi.data.api.data.indicator.Operator;

public interface FilterFunction {

    public static final int SEQUENCE_FILTER = 1;
    public static final int ACCUMULATED_FILTER = 2;

    public boolean verifyCondition(double originalValue);

    public void setOperator(Operator operator);

    public Operator getOperator();

    public void setValue(String valor);

    public String getValue();

    String getFormattedValue();

    public int getType();

    public String getFieldName();

    public String getFieldTitle();

    public Object clone();
}
