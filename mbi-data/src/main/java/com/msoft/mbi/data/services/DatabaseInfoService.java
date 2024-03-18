package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.data.DatabaseColumn;
import com.msoft.mbi.data.api.data.DatabaseTable;
import com.msoft.mbi.data.api.data.ForeignKey;
import com.msoft.mbi.data.api.data.PrimaryKey;

import java.util.List;

public interface DatabaseInfoService {

    List<DatabaseTable> getTables(String tenantId);

    List<DatabaseColumn> getColumns(String tenantId, String tableName);

    List<PrimaryKey> getPrimaryKeys(String tenantId, String tableName);

    List<ForeignKey> getForeignKeys(String tenantId, String tableName);

}
