package com.msoft.mbi.cube.multi.column;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;
import com.msoft.mbi.cube.util.logicOperators.LogicalOperators;

public class TextType implements DataType<String> {


    public static final String EMPTY = "";

    public String getValue(ResultSet set, String campo) throws SQLException {
        String text = set.getString(campo);
        return ((set.wasNull()) ? EMPTY : text.trim());
    }

    public String format(String t) {
        return t;
    }

    @Override
    public OperaTor<String> getOperator(String operator) {
        if (LogicalOperators.EQUAL.equals(operator)) {
            return LogicalOperators.OPERATOR_EQUAL_DIMENSION;
        } else if (LogicalOperators.STARTS_WITH.equals(operator)) {
            return LogicalOperators.OPERATOR_STATS_WITH_TEXT;
        } else if (LogicalOperators.CONTAINS.equals(operator)) {
            return LogicalOperators.OPERA_TOR_CONTAINS_TEXT;
        } else if (LogicalOperators.NOT_CONTAINS.equals(operator)) {
            return LogicalOperators.OPERA_TOR_NOT_CONTAINS_TEXT;
        } else {
            return LogicalOperators.OPERA_TOR_NOT_EQUALS_DIMENSION;
        }
    }

}
