package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.data.DatabaseColumn;
import com.msoft.mbi.data.api.data.DatabaseTable;
import com.msoft.mbi.data.api.data.ForeignKey;
import com.msoft.mbi.data.api.data.PrimaryKey;
import com.msoft.mbi.data.connection.ConnectionManager;
import com.msoft.mbi.data.services.DatabaseInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class DatabaseInfoServiceImpl implements DatabaseInfoService {

    private final ConnectionManager connectionManager;

    @Override
    public List<DatabaseTable> getTables(String tenantId) {

        try {
            ResultSet tablesSet = this.getTablesResultSet(tenantId);
            return this.getTablesFromResultSet(tablesSet);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public List<DatabaseColumn> getColumns(String tenantId, String tableName) {

        try {
            ResultSet columnSet = this.getColumnsResultSet(tenantId, tableName);
            return this.getColumnsFromResultSet(columnSet);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public List<PrimaryKey> getPrimaryKeys(String tenantId, String tableName) {
        try {
            ResultSet pksSet = this.getPKResultSet(tenantId, tableName);
            return this.getPKsFromResultSet(pksSet);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public List<ForeignKey> getForeignKeys(String tenantId, String tableName) {
        try {
            ResultSet fksSet = this.getFKResultSet(tenantId, tableName);
            return this.getFKsFromResultSet(fksSet);
        } catch (Exception exception) {
            return null;
        }
    }

    private ResultSet getTablesResultSet(String tenantId) throws SQLException {

        JdbcTemplate jdbcTemplate = connectionManager.getNewConnection(tenantId);

        DataSource dataSource = jdbcTemplate.getDataSource();
        String catalog = dataSource.getConnection().getCatalog();
        String schema = dataSource.getConnection().getSchema();
        DatabaseMetaData databaseMetaData = dataSource.getConnection().getMetaData();
        //String user = databaseMetaData.getUserName(); // Use user as catalog for DB2

        String[] types = {"TABLE", "VIEW"};

        return databaseMetaData.getTables(catalog, schema, "%", types);
    }

    private ResultSet getColumnsResultSet(String tenantId, String tableName) throws SQLException {

        JdbcTemplate jdbcTemplate = connectionManager.getNewConnection(tenantId);

        DataSource dataSource = jdbcTemplate.getDataSource();
        String catalog = dataSource.getConnection().getCatalog();
        DatabaseMetaData databaseMetaData = dataSource.getConnection().getMetaData();
        //String user = databaseMetaData.getUserName(); // Use user as catalog for DB2

        return databaseMetaData.getColumns(catalog, null, tableName, null);
    }

    private ResultSet getPKResultSet(String tenantId, String tableName) throws SQLException {

        JdbcTemplate jdbcTemplate = connectionManager.getNewConnection(tenantId);

        DataSource dataSource = jdbcTemplate.getDataSource();
        String catalog = dataSource.getConnection().getCatalog();
        DatabaseMetaData databaseMetaData = dataSource.getConnection().getMetaData();
        //String user = databaseMetaData.getUserName(); // Use user as catalog for DB2

        return databaseMetaData.getPrimaryKeys(catalog, null, tableName);
    }

    private ResultSet getFKResultSet(String tenantId, String tableName) throws SQLException {

        JdbcTemplate jdbcTemplate = connectionManager.getNewConnection(tenantId);

        DataSource dataSource = jdbcTemplate.getDataSource();
        String catalog = dataSource.getConnection().getCatalog();
        DatabaseMetaData databaseMetaData = dataSource.getConnection().getMetaData();
        //String user = databaseMetaData.getUserName(); // Use user as catalog for DB2

        return databaseMetaData.getImportedKeys(catalog, null, tableName);
    }

    private List<PrimaryKey> getPKsFromResultSet(ResultSet pkSet) throws SQLException {
        List<PrimaryKey> primaryKeys = new ArrayList<>();
        while(pkSet.next()) {
            primaryKeys.add(PrimaryKey.builder()
                    .tableName(pkSet.getString("TABLE_NAME"))
                    .columnName(pkSet.getString("COLUMN_NAME"))
                    .build());
        }

        return primaryKeys;
    }

    private List<ForeignKey> getFKsFromResultSet(ResultSet fkSet) throws SQLException {
        List<ForeignKey> foreignKeys = new ArrayList<>();
        while(fkSet.next()) {
            foreignKeys.add(ForeignKey.builder()
                    .pkTableName(fkSet.getString("PKTABLE_NAME"))
                    .pkColumnName(fkSet.getString("PKCOLUMN_NAME"))
                    .fkTableName(fkSet.getString("FKTABLE_NAME"))
                    .fkColumnName(fkSet.getString("FKCOLUMN_NAME"))
                    .build());
        }

        return foreignKeys;
    }
    private List<DatabaseTable> getTablesFromResultSet(ResultSet tablesSet) throws SQLException {
        List<DatabaseTable> databaseTables = new ArrayList<>();
        while(tablesSet.next()) {
            databaseTables.add(DatabaseTable.builder()
                .tableName(tablesSet.getString("TABLE_NAME"))
                .tableType(tablesSet.getString("TABLE_TYPE"))
                .build());
        }

        databaseTables.sort(Comparator.comparing(DatabaseTable::getTableName));
        return databaseTables;
    }

    private List<DatabaseColumn> getColumnsFromResultSet(ResultSet columnSet) throws SQLException {
        List<DatabaseColumn> databaseColumns = new ArrayList<>();
        while(columnSet.next()) {
            databaseColumns.add(DatabaseColumn.builder()
                .columnName(columnSet.getString("COLUMN_NAME"))
                .originalDataType(columnSet.getString("TYPE_NAME"))
                .columnSize(columnSet.getInt("COLUMN_SIZE"))
                .isNullable(columnSet.getString("IS_NULLABLE").equals("YES"))
                .build());
        }

        databaseColumns.sort(Comparator.comparing(DatabaseColumn::getColumnName));
        return databaseColumns;
    }


}
