package com.msoft.mbi.data.api.data.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionParameters {

    public static final String DRIVER_INFORMIX = "com.informix.jdbc.IfxDriver";
    public static final String DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";
    public static final String DRIVER_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String DRIVER_DB2 = "com.ibm.db2.jcc.DB2Driver";
    public static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
    public static final String DRIVER_OPENEDGE = "com.ddtek.jdbc.openedge.OpenEdgeDriver";

    private String name;
    private String user;
    private String password;

    private String driver;
    private String url;
    private String dateFormat;
    private String decimalSeparator;

    private String caseSensitive;

    ConnectionParameters(String name) {
        this.name = name;
    }

    public String getUrl() {
        if (this.url.contains("sqlserver") && this.url.contains("/database=")) {
            return this.url.replace("/database=", ";databaseName=");
        } else {
            return url;
        }

    }

    public String getDatabase() {
        return switch (this.getDriver()) {
            case DRIVER_INFORMIX -> "INFORMIX";
            case DRIVER_ORACLE -> "ORACLE";
            case DRIVER_SQLSERVER -> "SQLSERVER";
            case DRIVER_DB2 -> "DB2";
            case DRIVER_MYSQL -> "MYSQL";
            case DRIVER_OPENEDGE -> "OPENEDGE";
            default -> null;
        };
    }
}
