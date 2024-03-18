package com.msoft.mbi.data.api.data.util;

import java.sql.Date;
import java.sql.SQLException;

public interface BIPreparedStatement {

    public static final String PREPARE_STATEMENT_ORACLE    = "oracle.jdbc.driver.OraclePreparedStatementWrapper";
    public static final String PREPARE_STATEMENT_INFORMIX  = "com.informix.jdbc.IfxPreparedStatement";
    public static final String PREPARE_STATEMENT_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement";
    public static final String PREPARE_STATEMENT_DB2       = "com.ibm.db2.jcc.b.cf";

    public void setDate(int index, Date value) throws SQLException;

    public void setString(int index, String value) throws SQLException;

    public void setDouble(int index, Double valor) throws SQLException;
}
