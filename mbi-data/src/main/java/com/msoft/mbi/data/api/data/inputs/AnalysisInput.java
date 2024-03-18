package com.msoft.mbi.data.api.data.inputs;

import com.msoft.mbi.data.api.data.AnalysisPermission;
import com.msoft.mbi.data.api.dtos.BIAreaDTO;
import com.msoft.mbi.data.api.dtos.indicators.BIAnalysisFieldDTO;
import com.msoft.mbi.data.api.dtos.indicators.BIFromClauseDTO;
import com.msoft.mbi.data.api.dtos.indicators.BISearchClauseDTO;
import com.msoft.mbi.data.api.dtos.indicators.BIWhereClauseDTO;
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
    private String defaultDisplay = "T";
    private boolean usesSequence = false;
    private BIAreaDTO biAreaByArea;
    private List<BIAnalysisFieldDTO> biAnalysisFields;
    private BISearchClauseDTO biSearchClause;
    private BIFromClauseDTO biFromClause;
    private BIWhereClauseDTO biWhereClause;
    private List<AnalysisPermission> permissions;

}


