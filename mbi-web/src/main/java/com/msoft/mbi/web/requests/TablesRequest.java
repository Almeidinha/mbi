package com.msoft.mbi.web.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TablesRequest {
    private String tenantId;
    private String[] tableNames;
}
