package com.msoft.mbi.cube.multi.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.msoft.mbi.cube.multi.column.MaskColumnMetaData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("unused")
public class MetaDataField {

    private Integer id;
    private String name;
    private String title;
    private String dataType;
    private String fieldType;
    private String fieldNickname;
    private boolean expression;
    private String defaultField;
    private MetaDataField orderingField;
    private Integer order;
    private String tableNickname;
    private String orderDirection;
    private Integer numDecimalPositions;
    private boolean totalField;
    private String verticalAnalysisType;
    private boolean accumulatedParticipation;
    private boolean accumulatedValue;
    private Integer displayLocation;
    private Integer columnWidth = 100;
    private String columnAlignmentPosition;
    private String horizontalAnalysisType;
    private boolean totalLineField;
    private boolean accumulateLineField;
    private boolean totalPartial;
    private boolean mediaPartial;
    private boolean expressionInPartial;
    private boolean expressionInTotalPartial;
    private Integer stepAmount;
    private boolean horizontalParticipation;
    private boolean horizontalAccumulatedParticipation;
    private Integer accumulatedOrder;
    private String accumulatedOrderDirection;
    private boolean usesMediaLine;
    private boolean drillDown;
    private String aggregationType;
    private boolean showSequence;
    private String rankingExpression;
    private List<MaskColumnMetaData> fieldMask;
    private List<HTMLLineMask> htmlLineMasks;
    private String totalLinesType;
    private Integer sequence;
    private String nullValueMask;
    private List<ColorAlertMetadata> colorAlertMetadata;
    private List<ColorAlertMetadata> colorAlertMetadataSecondField;
    private Map<String, List<ColorAlertMetadata>> colorAlertMetadataRelatedFieldFunction;
    private Map<String, List<ColorAlertMetadata>> colorAlertMetadataRelatedSecondFieldFunction;
    private String aggregationApplyOrder;
    public static final int LINE = 1;
    public static final int COLUMN = 2;
    public static final String DIMENSION = "D";
    public static final String METRIC = "M";
    public static final String TEST_TYPE = "S";
    public static final String DATA_TYPE = "D";
    public static final String HOUR_TYPE = "H";
    public static final String INT_TYPE = "I";
    public static final String DECIMAL_TYPE = "N";
    public static final String AV_TYPE_NOT_APPLY = "N";
    public static final String AV_TYPE_GENERAL = "T";
    public static final String AV_TYPE_PARTIAL_NEXT_LEVEL = "H";
    public static final String AV_TYPE_PARTIAL_NEXT_LEVEL_TOTAL = "P";
    public static final String AH_TYPE_NOT_APPLY = "N";
    public static final String AH_TYPE_FIXED = "F";
    public static final String AH_TYPE_DYNAMIC = "D";
    public static final String NOT_TOTAL = "N";
    public static final String TOTAL_APPLY_SUM = "S";
    public static final String TOTAL_APPLY_EXPRESSION = "E";
    public static final String AGGREGATION_APPLY_BEFORE = "A";
    public static final String AGGREGATION_APPLY_AFTER = "D";
    public static final String ADDED_METRIC = "S";
    public static final String NOT_ADDED_METRIC = "N";
    public static final String METRIC_RESTRICTED_VIEW = "T";

    public MetaDataField() {
        this.fieldMask = new ArrayList<>();
        this.htmlLineMasks = new ArrayList<>();
        this.colorAlertMetadataRelatedFieldFunction = new HashMap<>();
        this.colorAlertMetadataRelatedSecondFieldFunction = new HashMap<>();
        this.colorAlertMetadata = new ArrayList<>();
        this.colorAlertMetadataSecondField = new ArrayList<>();
        this.order = 0;
        this.accumulatedOrder = 0;
        this.numDecimalPositions = 0;
        this.orderDirection = "ASC";
        this.accumulatedOrderDirection = "ASC";
        this.verticalAnalysisType = AV_TYPE_NOT_APPLY;
        this.horizontalAnalysisType = AH_TYPE_NOT_APPLY;
        this.aggregationApplyOrder = AGGREGATION_APPLY_BEFORE;
    }

    public boolean hasAccumulatedParticipation() {
        return accumulatedParticipation;
    }

    public boolean hasAccumulatedValue() {
        return accumulatedValue;
    }

    public boolean hasHorizontalParticipation() {
        return horizontalParticipation;
    }


    public boolean hasHorizontalAccumulatedParticipation() {
        return horizontalAccumulatedParticipation;
    }


    public void addMask(MaskColumnMetaData mask) {
        this.fieldMask.add(mask);
    }


    public void addHTMLLineMask(HTMLLineMask mask) {
        this.htmlLineMasks.add(mask);
    }




    public boolean hasVerticalAnalysis() {
        return !AV_TYPE_NOT_APPLY.equals(this.verticalAnalysisType);
    }

    public void addColorAlert(ColorAlertMetadata colorAlert, String alertType) {
        List<ColorAlertMetadata> colorAlerts;
        Map<String, List<ColorAlertMetadata>> colorAlertRelativeField;
        if (ColorAlertMetadata.VALUE_ALERT_TYPE.equals(alertType)) {
            colorAlerts = this.colorAlertMetadata;
            colorAlertRelativeField = this.colorAlertMetadataRelatedFieldFunction;
        } else {
            colorAlerts = this.colorAlertMetadataSecondField;
            colorAlertRelativeField = this.colorAlertMetadataRelatedSecondFieldFunction;
        }
        if (!colorAlert.isRelativeFieldFunction()) {
            colorAlerts.add(colorAlert);
        } else {
            String function = colorAlert.getFunction();
            List<ColorAlertMetadata> alertMetadataList = colorAlertRelativeField.get(function);
            if (alertMetadataList == null) {
                alertMetadataList = new ArrayList<>();
            }
            colorAlert.setFunction(ColorAlertMetadata.NO_FUNCTION);
            alertMetadataList.add(colorAlert);
            colorAlertRelativeField.put(function, alertMetadataList);
        }
    }

    public List<ColorAlertMetadata> getRelativeFieldFunctionValueAlert(String relativeFieldFunction) {
        return this.colorAlertMetadataRelatedFieldFunction.get(relativeFieldFunction);
    }

    public List<ColorAlertMetadata> getRelativeSecondFieldFunctionValueAlert(String relativeFieldFunction) {
        return this.colorAlertMetadataRelatedSecondFieldFunction.get(relativeFieldFunction);
    }

    @Override
    public String toString() {
        return this.title;
    }


    public boolean hasHorizontalAnalysis() {
        return !AH_TYPE_NOT_APPLY.equals(this.horizontalAnalysisType);
    }

    public HTMLLineMask getMascaraLinkHTMLByID(String idMascara) {
        for (HTMLLineMask mascara : this.htmlLineMasks) {
            if (idMascara.equals(mascara.getId())) {
                return mascara;
            }
        }
        return null;
    }
}
