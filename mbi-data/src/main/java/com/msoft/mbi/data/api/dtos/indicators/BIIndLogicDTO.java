package com.msoft.mbi.data.api.dtos.indicators;

import com.msoft.mbi.data.api.dtos.BaseDTO;
import com.msoft.mbi.data.api.dtos.filters.FiltersDTO;
import com.msoft.mbi.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class BIIndLogicDTO extends BaseDTO {

    private Integer id;

    private Integer companyId;

    private String name;

    private Integer areaId;

    private String fileName;

    private String graphTitle;

    private boolean scheduled;

    private int defaultGraph;

    private int lastUpdatedUser;

    private String comment;

    private boolean isFrozen;

    private String defaultDisplay;

    private UUID connectionId;

    private Integer numberOfSteps;

    private boolean usesSequence;

    private Integer tableType;

    private Integer originalIndicator;

    private boolean inheritsFields;

    private boolean inheritsRestrictions;

    private BISearchClauseDTO biSearchClause;

    private BIFromClauseDTO biFromClause;

    private BIWhereClauseDTO biWhereClause;

    private BIGroupClauseDTO biGroupClause;

    private BIOrderClauseDTO biOrderClause;

    private BIHavingClauseDTO biHavingClause;

    private BIFixedConditionClauseDTO biFixedConditionClause;

    private BIDimensionFilterDTO biDimensionFilter;

    private BIIndMetricFilterDTO biIndMetricFilter;

    private BIIndSqlMetricFilterDTO biIndSqlMetricFilter;

    private List<BIIndAlertColorDTO> biIndAlertColors;

    private List<BIAnalysisFieldDTO> biAnalysisFields;

    private List<BIColorConditionsDTO> biColorConditions;

    private FiltersDTO filtersDTO;

    private List<BIUserIndDTO> biUserIndicators;

    private List<BIUserGroupIndDTO> biUserGroupIndicators;
}
