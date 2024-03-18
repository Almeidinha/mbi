package com.msoft.mbi.data.api.data.util;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BIDefaultPreparedStatement implements BIPreparedStatement {

    private final PreparedStatement statement;

    public BIDefaultPreparedStatement(PreparedStatement statement){
        this.statement = statement;
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
