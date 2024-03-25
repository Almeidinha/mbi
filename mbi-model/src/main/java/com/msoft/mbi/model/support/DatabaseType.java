package com.msoft.mbi.model.support;

import java.util.stream.Stream;

public enum DatabaseType {
    MYSQL(0),
    MSSQL(1),
    ORACLE(2),
    POSTGRE(3),
    DB2(4),
    INFORMIX(5),
    OPENEDGE(6);

    private final int databaseType;

    private DatabaseType(int databaseType) {
        this.databaseType = databaseType;
    }
    public int getDatabaseType() {
        return databaseType;
    }

    public static DatabaseType of(int databaseType) {
        return Stream.of(DatabaseType.values())
            .filter(p -> p.getDatabaseType() == databaseType)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
