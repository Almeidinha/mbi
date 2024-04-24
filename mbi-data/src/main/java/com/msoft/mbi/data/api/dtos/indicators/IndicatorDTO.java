package com.msoft.mbi.data.api.dtos.indicators;

import com.msoft.mbi.data.api.dtos.filters.FiltersDTO;
import com.msoft.mbi.data.api.dtos.restrictions.MetricDimensionRestrictionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndicatorDTO {

    private Integer code;
    private String name;
    private Integer areaCode;
    private int companyId;
    private List<FieldDTO> fields;
    private FiltersDTO filters = new FiltersDTO();
    // private FiltersFunction filtersFunction; TODO: MAP THIS WHEN IMPLEMENTING FILTERS FUNCTION
    // private AnalysisComments analysisComments; TODO: MAP THIS WHEN IMPLEMENTING AnalysisComments
    // private List<AnalysisUserPermission> AnalysisUserPermissions; TODO: CODE THIS WHEN IMPLEMENTING ANALYSIS PERMISSIONS
    // private List<AnalysisGroupPermissions> analysisGroupPermissions; TODO: CODE THIS WHEN IMPLEMENTING ANALYSIS PERMISSIONS
    private String fileName;
    private String searchClause;
    private String fixedConditionClause;
    private String fromClause;
    private String whereClause;
    private String groupClause;
    private String orderClause;
    private String dimensionFilters;
    private String metricSqlFilters;
    private String metricFilters;
    private boolean scheduled = false;
    private Integer scheduledCode = null;
    private String filterTable;
    private boolean temporaryFrozenStatus;
    private boolean frozenStatus;
    private int panelCode;
    private String connectionId;
    private Integer databaseType;
    private UUID tenantId;
    private boolean multidimensional;
    private String currentView = "T";
    private String dateFormat = "dd/MM/yyyy";

    private int leftCoordinates = 10;
    private int topCoordinates = 60;
    private int height = 0;
    private int width = 0;
    private boolean isMaximized = true;
    private boolean isOpen = true;
    // private Restrictions restrictions; TODO: MAP THIS  WHEN IMPLEMENTING restrictions
    private PartialTotalizationsDTO partialTotalizations;
    private boolean usesSequence = false;
    private int tableType;
    private List<MetricDimensionRestrictionDTO> metricDimensionRestrictions;
    private ColorsAlertDTO colorAlerts = new ColorsAlertDTO();
    private int panelIndex;
    private boolean hasData;

    private int originalCode;
    private Integer originalIndicator;
    private boolean inheritsRestrictions = false;
    private boolean inheritsFields = false;
    private boolean replicateChanges;

}
