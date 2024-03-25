package com.msoft.mbi.data.api.dtos.indicators.entities;

import com.msoft.mbi.data.api.dtos.BIAreaDTO;
import com.msoft.mbi.data.api.dtos.BICompanyDTO;
import com.msoft.mbi.data.api.dtos.schedule.BIScheduleDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BIIndDTO {

    private Integer id;
    private String name;
    private String fileName;
    private String graphTitle;
    private boolean scheduled;
    private int defaultGraph;
    private int lastUpdatedUser;
    private String comment;
    private boolean frozen;
    private String defaultDisplay;
    private UUID connectionId;
    private Integer numberOfSteps;
    private boolean usesSequence;
    private Integer tableType;
    private Integer originalIndicator;
    private boolean inheritsFields;
    private boolean inheritsRestrictions;

    private BIAreaDTO biAreaByArea;
    private BICompanyDTO companyIdByCompany;
    private BISearchClauseDTO biSearchClause;
    private BIFromClauseDTO biFromClause;
    private BIWhereClauseDTO biWhereClause;
    private BIFixedConditionClauseDTO biFixedConditionClause;
    private BIDimensionFilterDTO biDimensionFilter;
    private BIIndMetricFilterDTO biIndMetricFilter;
    private BIIndSqlMetricFilterDTO biIndSqlMetricFilter;
    private BIGroupClauseDTO biGroupClause;
    private BIOrderClauseDTO biOrderClause;
    private BIHavingClauseDTO havingClause;
    private List<BIAnalysisFieldDTO> biAnalysisFields;
    private List<BIUserIndDTO> biUserIndicators;
    private List<BIUserGroupIndDTO> biUserGroupIndicators;
    private List<BIScheduleDTO> biSchedule;

    private List<BIIndAlertColorDTO> biIndAlertColors;

    // private List<BICommentsEntity> biComments; // TODO MAP WHEN IMPLEMENT COMMNETS
    // private List<BIColorConditionsEntity> biColorConditions; // TODO MAP WHEN IMPLEMENT COLOR CONDITIONS

    // private List<BIUserGroupRestrictionEntity> biUserGroupRestrictions; // TODO MAP WHEN IMPLEMENT RESTRICTIONS
    // private List<BIDimMetricRestrictionEntity> biDimMetricRestrictions; // TODO MAP WHEN IMPLEMENT RESTRICTIONS
    // private List<BIUserRestrictionEntity> biUserRestrictions; // TODO MAP WHEN IMPLEMENT RESTRICTIONS
    // private List<BIRestrictionEntity> biRestrictions; // TODO MAP WHEN IMPLEMENT RESTRICTIONS
}
