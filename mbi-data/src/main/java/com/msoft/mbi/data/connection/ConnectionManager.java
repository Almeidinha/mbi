package com.msoft.mbi.data.connection;

import com.msoft.mbi.data.services.BITenantService;
import com.msoft.mbi.model.BITenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConnectionManager {

    private final BITenantService tenantService;

    private enum DriverClassNames {
        MYSQL("com.mysql.cj.jdbc.Driver"),
        MSSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver"),
        ORACLE("oracle.jdbc.driver.OracleDriver"),
        POSTGRE("org.postgresql.Driver"),
        DB2("com.ibm.db2.jcc.DB2Driver"),
        INFORMIX("com.informix.jdbc.IfxDriver"),
        OPENEDGE("com.ddtek.jdbc.openedge.OpenEdgeDriver");

        public final String driverName;

        DriverClassNames(String driverName) {
            this.driverName = driverName;
        }
    }

    public JdbcTemplate getNewConnection(UUID biTenantId) {
        return getNewConnection(biTenantId.toString());
    }
    public JdbcTemplate getNewConnection(String biTenantId) {

        BITenantEntity biTenantEntity = tenantService.findById(UUID.fromString(biTenantId));

        DataSource dataSource = getDatasource(biTenantEntity);

        return new JdbcTemplate(dataSource);
    }

    public JdbcTemplate getNewConnection(BITenantEntity biTenant) {

        DataSource dataSource = getDatasource(biTenant);

        return new JdbcTemplate(dataSource);
    }

    public NamedParameterJdbcTemplate getNewNamedParamConnection(BITenantEntity biTenant) {

        DataSource dataSource = getDatasource(biTenant);

        return new NamedParameterJdbcTemplate(dataSource);
    }


    private DataSource getDatasource (BITenantEntity biTenant) {
        return DataSourceBuilder.create()
                .url(biTenant.getUrl()) // "jdbc:sqlserver://localhost:1433;databaseName=thomas;encrypt=true;trustServerCertificate=true;"
                .username(biTenant.getUsername()).password(biTenant.getPassword())
                .driverClassName(DriverClassNames.valueOf(biTenant.getDatabaseType().name()).driverName) // // "com.microsoft.sqlserver.jdbc.SQLServerDriver"
                .build();
    }
}
