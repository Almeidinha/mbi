package com.msoft.mbi.data.api.dtos.indicators.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BIAnalysisFieldDTO {

    private BiAnalysisFieldIdDTO id = new BiAnalysisFieldIdDTO();
    private String name;
    private String title;
    private String fieldType;
    private String dataType;
    private String nickname;
    private boolean expression;
    private Integer filterSequence;
    private Integer visualizationSequence;
    private String defaultField;
    private Integer fieldOrder;
    private String tableNickname;
    private String direction;
    private Integer decimalPositions;
    private String fieldTotalization;
    private String vertical;
    private String aggregationType;
    private boolean accumulatedParticipation;
    private String fieldColor;
    private String defaultGraph;
    private String ignoreZeros;
    private boolean accumulatedValue;
    private Integer localApres;
    private Integer columnWidth;
    private String columnAlignment;
    private String horizontal;
    private boolean lineFieldTotalization;
    private String accumulatedLineField;
    private String tendencyLine;
    private String tendencyLineColor;
    private String dateMask;
    private boolean partialTotalization;
    private Integer numberOfSteps;
    private Integer ganttGraphPosition;
    private String ganttGraphColor;
    private boolean horizontalParticipation;
    private boolean horizontalParticipationAccumulated;
    private Integer accumulatedOrder;
    private String accumulatedOrderDirection;
    private boolean usesMediaLine;
    private boolean fixedValue;
    private Integer locApresGraph;
    private String graphType;
    private String firstGraphType;
    private String secGraphType;
    private String referenceAxis;
    private Integer graphVisualizationSequence;
    private Integer originalAnalysisField;
    private boolean drillDown;
    private Integer generalFilter;
    private boolean mandatoryFilter;
    private Integer delegateOrder;

    // private Collection<BIIndAlertColorEntity> biColorAlertInds1; TODO: MAPP WHEN IMPLEMENTING ALERTS
    // private Collection<BIIndAlertColorEntity> biColorAlertInds2; TODO: MAPP WHEN IMPLEMENTING ALERTS
    // private Collection<BIColorConditionsEntity> biColorConditions; TODO: MAPP WHEN IMPLEMENTING COLORS
    // private Collection<BIDimMetricRestrictionEntity> biDimDimRestriction; TODO: MAPP WHEN IMPLEMENTING RESTRICTIONS
    // private Collection<BIDimMetricRestrictionEntity> biDimMetricRestriction; TODO: MAPP WHEN IMPLEMENTING RESTRICTIONS
}
