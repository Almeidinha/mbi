package com.msoft.mbi.cube.multi.column;

import java.io.Serial;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.msoft.mbi.cube.util.logicOperators.OperaTor;
import com.msoft.mbi.cube.util.logicOperators.LogicalOperators;

public class TipoTexto implements DataType<String> {

    @Serial
    private static final long serialVersionUID = -679817482559706559L;

    public static final String BRANCO = "";
    private String texto = null;

    public String getValor(ResultSet set, String campo) throws SQLException {
        this.texto = set.getString(campo);
        return ((set.wasNull()) ? BRANCO : this.texto.trim());
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
