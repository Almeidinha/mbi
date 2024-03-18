package com.msoft.mbi.cube.multi.column;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;
import com.msoft.mbi.cube.util.logicOperators.LogicalOperators;

public abstract class TipoNumero<T> implements DataType<T> {

    public static final Number BRANCO = 0;

    @Override
    public OperaTor getOperator(String operator) {
        if (LogicalOperators.GREATER_THAN.equals(operator)) {
            return LogicalOperators.OPERATOR_GREATER_NUMBER;
        } else if (LogicalOperators.GREATER_EQUAL.equals(operator)) {
            return LogicalOperators.OPERATOR_GREATER_EQUAL_NUMBER;
        } else if (LogicalOperators.EQUAL.equals(operator)) {
            return LogicalOperators.OPERA_TOR_IGUAL_NUMBER;
        } else if (LogicalOperators.LESS_THAN.equals(operator)) {
            return LogicalOperators.OPERATOR_LESS_THAN_NUMBER;
        } else if (LogicalOperators.LESS_EQUAL.equals(operator)) {
            return LogicalOperators.OPERATOR_LESS_EQUAL_NUMBER;
        } else if (LogicalOperators.BETWEEN_EXCLUSIVE.equals(operator)) {
            return LogicalOperators.OPERATOR_BETWEEN_NUMBERS;
        } else if (LogicalOperators.BETWEEN_INCLUSIVE.equals(operator)) {
            return LogicalOperators.OPERATOR_BETWEEN_NUMBERS;
        } else {
            return LogicalOperators.OPERA_TOR_NOT_EQUALS_NUMBER;
        }
    }

}
