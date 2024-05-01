package com.msoft.mbi.cube.multi.coloralertcondition;

import com.msoft.mbi.cube.multi.column.DataType;
import com.msoft.mbi.cube.util.logicOperators.OperaTor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ColorAlertConditions {

    private int sequence;
    private ColorAlertProperties alertProperty;
    private String function;
    private int action;
    protected OperaTor<Object> operator;

    public ColorAlertConditions(int sequence, ColorAlertProperties alertProperty, String function, int action, String operator, DataType<Double> dataType) {
        this.sequence = sequence;
        this.alertProperty = alertProperty;
        this.function = function;
        this.action = action;
        this.setOperator(dataType, operator);
    }

    public void setOperator(DataType dataType, String operator) {
        this.operator = dataType.getOperator(operator);
    }


    public abstract boolean testCondition(Object value);

}
