package com.msoft.mbi.cube.multi.column;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;
import com.msoft.mbi.cube.util.logicOperators.LogicalOperators;

public abstract class TypeNumber<T> implements DataType<T> {

    public static final Number BRANCO = 0;

    @Override
    public OperaTor<T> getOperator(String operator) {
        return switch (operator) {
            case LogicalOperators.GREATER_THAN -> castOperaTor(LogicalOperators.OPERATOR_GREATER_NUMBER);
            case LogicalOperators.GREATER_EQUAL -> castOperaTor(LogicalOperators.OPERATOR_GREATER_EQUAL_NUMBER);
            case LogicalOperators.EQUAL -> castOperaTor(LogicalOperators.OPERA_TOR_IGUAL_NUMBER);
            case LogicalOperators.LESS_THAN -> castOperaTor(LogicalOperators.OPERATOR_LESS_THAN_NUMBER);
            case LogicalOperators.LESS_EQUAL -> castOperaTor(LogicalOperators.OPERATOR_LESS_EQUAL_NUMBER);
            case LogicalOperators.BETWEEN_EXCLUSIVE, LogicalOperators.BETWEEN_INCLUSIVE -> castOperaTor(LogicalOperators.OPERATOR_BETWEEN_NUMBERS);
            default -> castOperaTor(LogicalOperators.OPERA_TOR_NOT_EQUALS_NUMBER);
        };
    }

    @SuppressWarnings("unchecked")
    private <U> OperaTor<T> castOperaTor(OperaTor<U> operaTor) {
        return (OperaTor<T>) operaTor;
    }

}
