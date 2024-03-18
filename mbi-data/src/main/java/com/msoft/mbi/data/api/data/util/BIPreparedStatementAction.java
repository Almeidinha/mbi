package com.msoft.mbi.data.api.data.util;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.msoft.mbi.data.api.data.util.BIPreparedStatement.PREPARE_STATEMENT_ORACLE;

public class BIPreparedStatementAction {

    private final BIPreparedStatement statement;

    public BIPreparedStatementAction(PreparedStatement statement) {
        String classe = statement.getClass().getName();
        if (PREPARE_STATEMENT_ORACLE.equalsIgnoreCase(classe)) {
            this.statement = new BIOraclePreparedStatement(statement);
        } else {
            this.statement = new BIDefaultPreparedStatement(statement);
        }
    }

    public void setDate(int index, Date value) throws SQLException {
        this.statement.setDate(index, value);
    }

    public void setDouble(int index, Double valor) throws SQLException {
        this.statement.setDouble(index, valor);
    }

    public void setString(int index, String value) throws SQLException {
        this.statement.setString(index, value);
    }
}
