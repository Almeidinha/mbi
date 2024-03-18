package com.msoft.mbi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msoft.mbi.model.support.DatabaseType;
import com.msoft.mbi.model.support.OnPut;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bi_tenants")
public class BITenantEntity {

    @Null(groups = OnPut.class)
    @JsonProperty("tenantId")
    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @Column(name = "connection_name", columnDefinition = "text", nullable = false)
    private String connectionName;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Basic
    @Column(name = "database_type_value", nullable = false)
    private int databaseTypeValue;

    @Column(nullable = false, columnDefinition = "text")
    private String url;

    @Column(nullable = false, columnDefinition = "text")
    private String host;

    @Column(columnDefinition = "text")
    private String instance;

    @Column(name = "database_name", columnDefinition = "text")
    private String databaseName;

    @Column(columnDefinition = "text")
    private String service;

    @Column(name = "informix_server", columnDefinition = "text")
    private String informixServer;

    @Column(name = "informix_online")
    private boolean informixOnline;

    @Column
    private Integer port;

    @Column(columnDefinition = "text")
    private String sid;

    @Column(name= "date_format", columnDefinition = "text", nullable = false)
    private String dateFormat;

    @Column(name = "decimal_separator", columnDefinition = "text", length = 1, nullable = false)
    private String decimalSeparator;

    @Column(name = "extra_properties", columnDefinition = "text")
    private String extraProperties;

    @Column(nullable = false, columnDefinition = "text")
    private String username;

    @Column(nullable = false, columnDefinition = "text")
    private String password;

    @Transient
    private DatabaseType databaseType;

    @PostLoad
    void fillTransient() {
        if (databaseTypeValue > 0) {
            this.databaseType = DatabaseType.of(databaseTypeValue);
        }
    }

}
