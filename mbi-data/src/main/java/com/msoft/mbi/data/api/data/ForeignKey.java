package com.msoft.mbi.data.api.data;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ForeignKey {

    @Builder
    public ForeignKey(String pkTableName, String pkColumnName, String fkTableName, String fkColumnName) {
        this.UID = UUID.randomUUID();
        this.pkTableName = pkTableName;
        this.pkColumnName = pkColumnName;
        this.fkTableName = fkTableName;
        this.fkColumnName = fkColumnName;
    }

    private UUID UID;

    private String pkTableName;

    private String pkColumnName;

    private String fkTableName;

    private String fkColumnName;
}
