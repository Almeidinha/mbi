package com.msoft.mbi.cube.multi.dimension;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.column.DataType;
import com.msoft.mbi.cube.multi.column.TypeDate;
import com.msoft.mbi.cube.multi.column.TypeDecimal;
import com.msoft.mbi.cube.multi.column.TypoDimensionInt;
import com.msoft.mbi.cube.multi.column.TypeHour;
import com.msoft.mbi.cube.multi.column.TextType;
import com.msoft.mbi.cube.multi.coloralertcondition.ColorAlertConditionsDimensao;
import com.msoft.mbi.cube.multi.coloralertcondition.ColorAlertConditionsValorDimensao;
import com.msoft.mbi.cube.multi.coloralertcondition.ColorAlertProperties;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensionComparator;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensionDefaultComparator;
import com.msoft.mbi.cube.multi.generation.Printer;
import com.msoft.mbi.cube.multi.metadata.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metadata.MetaDataField;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DimensionMetaData extends ColumnMetaData {

    private DimensionMetaData parent = null;
    @Setter(AccessLevel.NONE)
    private String column;
    private DimensionMetaData orderingDimension = null;
    @Setter(AccessLevel.NONE)
    private int orderingType = 1;
    private int orderingSequence;
    private boolean totalPartial = false;
    private boolean mediaPartial = false;
    private boolean expressionPartial = false;
    @Getter(AccessLevel.NONE)
    private boolean isDrillDown = false;
    @Setter(AccessLevel.NONE)
    private DimensionMetaData child = null;
    private Cube cube = null;
    @Getter(AccessLevel.NONE)
    private final DataType<?> dataType;
    private int lowerLevelsCount = 0;
    private int lowerLevelSequenceCount = 0;
    private final List<ColorAlertConditionsDimensao> colorAlertLines;
    @Setter(AccessLevel.NONE)
    private final List<ColorAlertConditionsDimensao> colorAlertCells;
    private DimensionComparator comparator;
    private DimensionMetaData upperLevelTotal = null;
    private int referenceAxis = LINE;
    public static final int LINE = 1;
    public static final int COLUMN = 2;

    private MetaDataField metadataField = null;

    protected DimensionMetaData(String title, String column, DataType<?> type) {
        super(title, MetaDataField.DIMENSION);
        this.column = column;
        this.dataType = type;
        this.colorAlertCells = new ArrayList<>();
        this.colorAlertLines = new ArrayList<>();
        this.comparator = DimensionDefaultComparator.getInstance();
    }

    public void setAscending(boolean asc) {
        this.orderingType = ((asc) ? 1 : -1);
    }

    public boolean isLast() {
        return this.child == null;
    }

    public void setChild(DimensionMetaData child) {
        this.child = child;
        if (child.hasSequenceFields()) {
            incrementLevels(2);
        } else {
            incrementLevels(1);
        }
    }

    public boolean isLine() {
        return this.referenceAxis == LINE;
    }

    @SuppressWarnings("rawtypes")
    public DataType getDataType() {
        return dataType;
    }

    public boolean isDrillDown() {
        return isDrillDown;
    }

    public void setDrillDown(boolean isDrillDown) {
        this.isDrillDown = isDrillDown;
    }

    public void setUpperLevelTotal() {
        this.setUpperLevelTotal(this.searchUpperLevelTotal());
    }

    private void addColorAlertLine(ColorAlertConditionsDimensao colorAlert) {
        this.colorAlertLines.add(colorAlert);
    }

    private void addColorAlertCell(ColorAlertConditionsDimensao colorAlert) {
        this.colorAlertCells.add(colorAlert);
    }

    private void addColorAlert(ColorAlertConditionsDimensao colorAlert) {
        if (ColorAlertMetadata.PAINT_LINE_ACTION == colorAlert.getAction()) {
            this.addColorAlertLine(colorAlert);
        } else {
            this.addColorAlertCell(colorAlert);
        }
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    public static DimensionMetaData factory(MetaDataField metaDataField) {
        String title = metaDataField.getTitle();
        String column = metaDataField.getFieldNickname() != null ? metaDataField.getFieldNickname() : metaDataField.getName();
        DataType<?> dataType = getDataType(metaDataField.getDataType());

        DimensionMetaData dimensionMetaData = new DimensionMetaData(title, column, dataType);
        dimensionMetaData.setMetadataField(metaDataField);

        if (metaDataField.getOrderingField() != null) {
            DimensionMetaData metaData = factory(metaDataField.getOrderingField());
            dimensionMetaData.setOrderingDimension(metaData);
        }

        ColumnMetaData.factory(dimensionMetaData, metaDataField);
        dimensionMetaData.setAscending(!("DESC".equals(metaDataField.getOrderDirection()) && metaDataField.getOrder() > 0));

        addColorAlerts(metaDataField.getColorAlertMetadata(), dimensionMetaData);

        dimensionMetaData.setDrillDown(metaDataField.isDrillDown());
        dimensionMetaData.setOrderingSequence(metaDataField.getOrder());
        dimensionMetaData.setTotalPartial(metaDataField.isTotalPartial());
        dimensionMetaData.setMediaPartial(metaDataField.isMediaPartial());
        dimensionMetaData.setExpressionPartial(metaDataField.isExpressionInPartial());
        dimensionMetaData.setPartialTotalExpression(metaDataField.isExpressionInTotalPartial());

        return dimensionMetaData;
    }

    private static DataType<?> getDataType(String dataType) {
        return switch (dataType) {
            case MetaDataField.TEST_TYPE -> new TextType();
            case MetaDataField.DATA_TYPE -> new TypeDate();
            case MetaDataField.HOUR_TYPE -> new TypeHour();
            case MetaDataField.INT_TYPE -> new TypoDimensionInt();
            case MetaDataField.DECIMAL_TYPE -> new TypeDecimal();
            default -> null;
        };
    }

    private static void addColorAlerts(List<ColorAlertMetadata> alerts, DimensionMetaData dimensionMetaData) {
        for (ColorAlertMetadata alert : alerts) {
            ColorAlertProperties alertProperties = ColorAlertProperties.factory(alert.getFontColor(), alert.getBackGroundColor(), alert.getFontStyle(),
                    alert.isBold(), alert.isItalic(), alert.getFontSize());
            alertProperties.setAlignment(CellProperty.ALIGNMENT_LEFT);
            ColorAlertConditionsValorDimensao colorCondition = new ColorAlertConditionsValorDimensao(alert.getSequence(), alertProperties, alert.getFunction(),
                    alert.getAction(), alert.getOperator(), dimensionMetaData, alert.getValues());
            dimensionMetaData.addColorAlert(colorCondition);
        }
    }

    private DimensionMetaData searchUpperLevelTotal() {
        if (this.getParent() == null) {
            return this;
        }
        if (!this.getParent().isTotalPartial() && !this.getParent().isMediaPartial() && !this.getParent().isExpressionPartial()) {
            return this.getParent().searchUpperLevelTotal();
        }
        return this.getParent();
    }

    private void incrementLevels(int lowerLevelsWithSequence) {
        this.lowerLevelsCount++;
        this.lowerLevelSequenceCount += lowerLevelsWithSequence;
        if (this.parent != null) {
            parent.incrementLevels(lowerLevelsWithSequence);
        }
    }

    @Override
    public String getTextNullValue() {
        return "";
    }

    @Override
    public void printFieldTypeValue(Object valor, String cellProperty, Printer printer) {
        printer.printColumnValue(cellProperty, valor, this);
    }

    public String getDefaultStyle() {
        return CellProperty.CELL_PROPERTY_DIMENSION_VALUE;
    }

}
