package com.msoft.mbi.data.api.data;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class PrimaryKey {

    @Builder
    public PrimaryKey(String tableName, String columnName) {
        this.UID = UUID.randomUUID();
        this.tableName = tableName;
        this.columnName = columnName;
    }

    private UUID UID;

    private String tableName;

    private String columnName;


}
