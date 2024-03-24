package com.msoft.mbi.cube.multi.column;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;
import com.msoft.mbi.cube.util.logicOperators.LogicalOperators;

public class TypeDate implements DataType<Date> {


    public static final Date BRANCO = new Date(0);

    public Date getValue(ResultSet set, String campo) throws SQLException {
        return set.getDate(campo);
    }

    public Date format(Date date) {
        if (date == BRANCO || date == null) {
            return null;
        }
        return date;
    }

    @Override
    public OperaTor<Date> getOperator(String operator) {
        if (LogicalOperators.GREATER_THAN.equals(operator)) {
            return LogicalOperators.OPERATOR_GREATER_DATE;
        } else if (LogicalOperators.GREATER_EQUAL.equals(operator)) {
            return LogicalOperators.OPERATOR_GREATER_EQUAL_DATE;
        } else if (LogicalOperators.EQUAL.equals(operator)) {
            return LogicalOperators.OPERATOR_EQUAL_DIMENSION;
        } else if (LogicalOperators.LESS_THAN.equals(operator)) {
            return LogicalOperators.OPERATOR_LESS_THAN_DATE;
        } else if (LogicalOperators.LESS_EQUAL.equals(operator)) {
            return LogicalOperators.OPERATOR_LESS_EQUAL_DATE;
        } else if (LogicalOperators.BETWEEN_EXCLUSIVE.equals(operator)) {
            return LogicalOperators.OPERA_TOR_BETWEEN_EXCLUSIVE_DATA;
        } else if (LogicalOperators.BETWEEN_INCLUSIVE.equals(operator)) {
            return LogicalOperators.OPERA_TOR_BETWEEN_INCLUSIVE_DATA;
        } else {
            return LogicalOperators.OPERA_TOR_NOT_EQUALS_DIMENSION;
        }
    }
}
