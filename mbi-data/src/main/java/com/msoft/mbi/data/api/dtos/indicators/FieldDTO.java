package com.msoft.mbi.data.api.dtos.indicators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldDTO {

    private Integer fieldId;
    protected Integer indicatorId;
    private String name;
    private String title;
    private String nickname;
    private boolean expression = false;
    private int drillDownSequence;
    private int visualizationSequence;
    private String defaultField = "";
    private int order;
    private Integer delegateOrder;
    private String tableNickname;
    private String orderDirection = "ASC";
    private int numDecimalPositions;
    private boolean totalizingField;
    private boolean verticalAnalysis;
    private String verticalAnalysisType;
    private boolean horizontalAnalysis;
    private String horizontalAnalysisType;
    private String aggregationType;
    private boolean accumulatedParticipation;
    private boolean accumulatedValue;
    // private List<LineColor> lineColors; TODO: MAP THIS
    private boolean lastColorValueList;
    private String dataType;
    private String fieldType;
    private int displayLocation;
    private int columnWidth;
    private String columnAlignment;
    private boolean sumLine;
    private String accumulatedLine = "N";
    private String dateMask;
    private boolean partialTotalization;
    private boolean partialMedia;
    private boolean partialExpression;
    private boolean partialTotalExpression;
    private boolean applyTotalizationExpression;
    private int generalFilter;
    private boolean requiredField;

    private boolean horizontalParticipation;
    private boolean horizontalParticipationAccumulated;
    private int accumulatedOrder;
    private String accumulatedOrderDirection = "ASC";
    private boolean mediaLine;
    private boolean childField;
    private boolean fixedValue = false;
    // private FieldColorValues fieldColorValues; TODO: MAP THIS
    private boolean calculatorPerRestriction = false;
    private boolean replicateChanges;
    private List<FieldDTO> dependentCalculatedFields;
    private boolean navigableUpwards;

    private boolean drillDown;
    private boolean drillUp;
    private boolean navigable;
    private boolean deleted;

    private int numberOfSteps;
}
