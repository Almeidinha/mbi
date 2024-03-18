package com.msoft.mbi.cube.multi.column;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;
import com.msoft.mbi.cube.util.logicOperators.LogicalOperators;

public class TipoData implements DataType<Date> {


    private Date data = null;
    public static final Date BRANCO = new Date(Integer.MIN_VALUE - 1);

    public Date getValor(ResultSet set, String campo) throws SQLException {
        this.data = set.getDate(campo);
        return this.data;
    }

    public Date format(Date t) {
        if (t == BRANCO || t == null) {
            return null;
        }
        return t;
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
