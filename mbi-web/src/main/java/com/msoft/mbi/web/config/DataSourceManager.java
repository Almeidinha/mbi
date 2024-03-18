package com.msoft.mbi.web.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

// @Component
// @RefreshScope
public class DataSourceManager {

    private DataSource dataSource;

    @Value("${dburl}")
    private String dbUrl;

    @Value("${dbusername}")
    private String dbUserName;

    @Value("${dbpassword}")
    private String dbPassword;

    @PostConstruct
    public void reload() {
        // init the datasource
    }

    public DataSource getDataSource(String dbName) {
        return dataSource;
    }

    @Bean
    // @RefreshScope
    public DataSource getDatasource() {
        DataSource dataSource = DataSourceBuilder.create().url(dbUrl).username(dbUserName).password(dbPassword).build();
        return dataSource;
    }
}
