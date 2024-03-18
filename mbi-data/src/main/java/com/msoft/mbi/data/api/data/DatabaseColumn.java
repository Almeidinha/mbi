package com.msoft.mbi.data.api.data;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DatabaseColumn implements Comparable<DatabaseColumn> {


    @Builder
    public DatabaseColumn(String columnName, String originalDataType, Integer columnSize, boolean isNullable) {
        this.UID = UUID.randomUUID();
        this.columnName = columnName;
        this.originalDataType = originalDataType;
        this.columnSize = columnSize;
        this.isNullable = isNullable;
    }

    private String columnName;

    private String userDataType;

    private String originalDataType;

    private int columnSize;

    private boolean isNullable;

    private UUID UID;


    @Override
    public int compareTo(DatabaseColumn o) {
        return o.getColumnName().toLowerCase().compareTo(this.getColumnName().toLowerCase());
    }
}
