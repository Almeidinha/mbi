package com.msoft.mbi.data.api.data.inputs;

import com.msoft.mbi.data.api.data.AnalysisPermission;
import com.msoft.mbi.data.api.dtos.BIAreaDTO;
import com.msoft.mbi.data.api.dtos.indicators.FieldDTO;
import com.msoft.mbi.data.api.dtos.indicators.entities.BIAnalysisFieldDTO;
import com.msoft.mbi.data.api.dtos.indicators.entities.BIFromClauseDTO;
import com.msoft.mbi.data.api.dtos.indicators.entities.BISearchClauseDTO;
import com.msoft.mbi.data.api.dtos.indicators.entities.BIWhereClauseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnalysisInput {

    private Integer id;
    private String connectionId;
    private Integer userId;
    private String name;
    private Integer tableType = 1;
    private String defaultDisplay = "T";
    private boolean usesSequence = false;
    private BIAreaDTO biAreaByArea;
    private List<FieldDTO> fields;
    private BISearchClauseDTO biSearchClause;
    private BIFromClauseDTO biFromClause;
    private BIWhereClauseDTO biWhereClause;
    private List<AnalysisPermission> permissions;

}


