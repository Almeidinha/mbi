package com.msoft.mbi.data.api.data.util;

import oracle.jdbc.OraclePreparedStatement;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BIOraclePreparedStatement implements BIPreparedStatement {

    private final OraclePreparedStatement statement;

    public BIOraclePreparedStatement(PreparedStatement statement) {
        this.statement = (OraclePreparedStatement) statement;
    }

    public void setDate(int index, Date value) throws SQLException {
        this.statement.setDate(index, value);
    }

    public void setDouble(int index, Double value) throws SQLException {
        this.statement.setDouble(index, value);
    }

    public void setString(int index, String value) throws SQLException {
        this.statement.setFixedCHAR(index, value);
    }
}
