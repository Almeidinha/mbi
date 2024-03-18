package com.msoft.mbi.web.controllers;

import com.msoft.mbi.data.api.data.DatabaseColumn;
import com.msoft.mbi.data.api.data.DatabaseTable;
import com.msoft.mbi.data.services.DatabaseInfoService;
import com.msoft.mbi.web.requests.TablesRequest;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/database")
@RequiredArgsConstructor
public class DatabaseInfoController {

    private final DatabaseInfoService databaseInfoService;

    @GetMapping("/tables")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DatabaseTable>> getDatabaseTables(
            @PathParam("tenantId") String tenantId) {

        List<DatabaseTable> databaseTables = databaseInfoService.getTables(tenantId);

        return ResponseEntity.ok(databaseTables);
    }

    @PostMapping("/tables")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Map<String, List<DatabaseColumn>>>> getDatabaseTablesWithColumns(@RequestBody TablesRequest tablesRequest) {

        List<Map<String, List<DatabaseColumn>>> tableColumns = new ArrayList<>();

        for (String tableName: tablesRequest.getTableNames()) {
            List<DatabaseColumn> columns = databaseInfoService.getColumns(tablesRequest.getTenantId(), tableName);
            tableColumns.add(new HashMap<>(){{
                put(tableName, columns);
            }});
        }

        return ResponseEntity.ok(tableColumns);
    }

    @PostMapping("/tables/full")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DatabaseTable>> getTablesWithColumnsAndKeys(@RequestBody TablesRequest tablesRequest) {


        List<DatabaseTable> tables = new ArrayList<>();

        for (String tableName: tablesRequest.getTableNames()) {
            DatabaseTable table = DatabaseTable.builder().tableName(tableName).build();
            table.setColumns(databaseInfoService.getColumns(tablesRequest.getTenantId(), tableName));
            table.setPrimaryKeys(databaseInfoService.getPrimaryKeys(tablesRequest.getTenantId(), tableName));
            table.setForeignKeys(databaseInfoService.getForeignKeys(tablesRequest.getTenantId(), tableName));

            tables.add(table);
        }

        return ResponseEntity.ok(tables);
    }


    @GetMapping("/tables/columns")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Map<String, List<DatabaseColumn>>>> getDatabaseTableColumns(
            @PathParam("tenantId") String tenantId,
            @PathParam("tableName") String[] tableNames) {

        List<Map<String, List<DatabaseColumn>>> tableColumns = new ArrayList<>();

        for (String tableName: tableNames) {
            List<DatabaseColumn> columns = databaseInfoService.getColumns(tenantId, tableName);
            tableColumns.add(new HashMap<>(){{
                put(tableName, columns);
            }});
        }

        return ResponseEntity.ok(tableColumns);
    }

    @GetMapping("/columns")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DatabaseColumn>> getDatabaseColumns(
            @PathParam("tenantId") String tenantId,
            @PathParam("tableName") String tableName) {

        List<DatabaseColumn> databaseColumns = databaseInfoService.getColumns(tenantId, tableName);

        return ResponseEntity.ok(databaseColumns);
    }
}
