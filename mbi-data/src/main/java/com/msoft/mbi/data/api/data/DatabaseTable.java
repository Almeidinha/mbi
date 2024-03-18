package com.msoft.mbi.data.api.data;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class DatabaseTable implements Comparable<DatabaseTable> {


    @Builder
    public DatabaseTable (String tableName, String tableType) {
        this.UID = UUID.randomUUID();
        this.tableName = tableName != null ? tableName.trim() : null;
        this.tableType = tableType != null ? tableType.trim() : null;
    }

    private UUID UID;

    private String tableName;

    private String tableType;

    private List<DatabaseColumn> columns;

    private List<PrimaryKey> primaryKeys;

    private List<ForeignKey> foreignKeys;

    @Override
    public int compareTo(DatabaseTable o) {
        return this.getTableName().toUpperCase().compareTo(o.getTableName().toUpperCase());
    }
}
