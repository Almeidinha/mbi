package com.msoft.mbi.cube.multi.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.column.DataType;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetricaValor;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertProperties;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.generation.Printer;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;
import com.msoft.mbi.cube.multi.renderers.DecimalMaskRender;
import com.msoft.mbi.cube.multi.renderers.NullValueMask;
import com.msoft.mbi.cube.multi.partialTotalization.PartialTotalizationApplyType;
import com.msoft.mbi.cube.multi.partialTotalization.PartialTotalizationApplyTypeExpressao;
import com.msoft.mbi.cube.multi.partialTotalization.PartialTotalizationApplyTypeSoma;
import lombok.Getter;
import lombok.Setter;

public abstract class MetricMetaData extends ColumnMetaData {

    @Setter
    @Getter
    private DataType<Double> type;
    @Getter
    @Setter
    private Cube cube = null;
    @Getter
    @Setter
    private boolean viewed = true;
    @Getter
    @Setter
    private boolean totalPartialLines = false;
    @Getter
    @Setter
    private boolean mediaPartialLines = false;
    @Setter
    @Getter
    private boolean expressionPartialLines = false;
    @Setter
    @Getter
    private boolean totalPartialColumns = false;
    @Getter
    @Setter
    private boolean mediaPartialColumns = false;
    @Setter
    @Getter
    private boolean expressionPartialColumns = false;
    @Setter
    @Getter
    private boolean totalLines = false;
    @Setter
    @Getter
    private boolean totalSumColumns = false;
    @Getter
    @Setter
    private boolean totalMediaColumns = false;
    @Getter
    @Setter
    private boolean totalSumGeneralColumns = false;
    @Getter
    @Setter
    private boolean usePercent = false;
    private DecimalMaskRender decimalMaskRender;
    @Setter
    private NullValueMask nullValueMask;
    private final Map<String, List<ColorAlertConditionsMetrica>> lineColorAlertConditions;
    private final Map<String, List<ColorAlertConditionsMetrica>> cellColorAlertConditions;
    private PartialTotalizationApplyType partialTotalizationApplyType;
    @Getter
    private int decimalPlaces = 0;
    public static final String SUM_AGGREGATION = "SUM";
    public static final String COUNT_AGGREGATION = "COUNT";
    public static final String MEDIA_AGGREGATION = "AVG";
    public static final String MINIMUM_AGGREGATION = "MIN";
    public static final String MAX_AGGREGATION = "MAX";
    public static final String EMPTY = "EMPTY";
    @Getter
    private final List<MetricOrdering> metricOrderings;
    @Setter
    protected String aggregationType;
    public static final String TOTAL_AH = "totalizacaoHorizontal";
    public static final String MEDIA_AH = "mediaHorizontal";
    public static final String ACCUMULATED_VALUE_AH = "acumuladoHorizontal";
    public static final String TOTAL_AV = "totalizacaoVertical";
    public static final String TOTAL_PARTIAL = "totalizacaoParcial";
    public static final String MEDIA_PARTIAL = "mediaParcial";
    public static final String EXPRESSION_PARTIAL = "expressaoParcial";
    public static final String TOTAL_GENERAL = "totalizacao";

    protected MetricMetaData(String title, DataType<Double> type) {
        super(title, MetaDataField.METRIC);
        this.type = type;
        this.cellColorAlertConditions = new HashMap<>();
        this.lineColorAlertConditions = new HashMap<>();
        this.nullValueMask = new NullValueMask();
        this.decimalMaskRender = new DecimalMaskRender();
        this.partialTotalizationApplyType = PartialTotalizationApplyTypeSoma.getInstance();
        this.metricOrderings = new ArrayList<>();
        this.aggregationType = SUM_AGGREGATION;
    }

    public static void factory(MetricMetaData metricMetaData, MetaDataField metaDataField) {
        ColumnMetaData.factory(metricMetaData, metaDataField);
        if (metaDataField.getOrder() > 0) {
            MetricOrdering metricOrdering = new MetricOrdering(metaDataField.getOrderDirection(), metaDataField.getOrder(),
                    metricMetaData.getTitle(), MetricaValorUtilizarLinhaMetrica.getInstance());
            metricMetaData.addMetricOrdering(metricOrdering);
        }
        if (metaDataField.getAccumulatedOrder() > 0) {
            MetricOrdering metricOrdering = new MetricOrdering(metaDataField.getAccumulatedOrderDirection(), metaDataField.getAccumulatedOrder(),
                    metricMetaData.getTitle(), MetricaValorUtilizarTotal.getInstance());
            metricMetaData.addMetricOrdering(metricOrdering);
        }
        if (metaDataField.getNullValueMask() != null) {
            metricMetaData.setNullValueMask(new NullValueMask(metaDataField.getNullValueMask()));
        }
        factoryColorsAlert(metricMetaData, metaDataField.getColorAlertMetadata());

        metricMetaData.setDecimalPlaces(metaDataField.getNumDecimalPositions());
        metricMetaData.setTotalSumColumns(metaDataField.isAccumulateLineField());
        metricMetaData.setTotalSumGeneralColumns(metaDataField.isTotalLineField());
        metricMetaData.setTotalLines(metaDataField.isTotalField());
        metricMetaData.setTotalPartialLines(metaDataField.isTotalPartial());
        metricMetaData.setTotalPartialColumns(metaDataField.isTotalPartial());

        metricMetaData.setMediaPartialColumns(metaDataField.isMediaPartial());
        metricMetaData.setMediaPartialLines(metaDataField.isMediaPartial());
        metricMetaData.setExpressionPartialColumns(metaDataField.isExpressionInPartial());
        metricMetaData.setExpressionPartialLines(metaDataField.isExpressionInPartial());
        metricMetaData.setPartialTotalExpression(metaDataField.isExpressionInTotalPartial());
        metricMetaData.setTotalMediaColumns(metaDataField.isUsesMediaLine());
        metricMetaData.setAggregationType(metaDataField.getAggregationType());
    }

    protected static void factoryColorsAlert(MetricMetaData metricMetaData, List<ColorAlertMetadata> alerts) {
        ColorAlertProperties propriedadeAlerta;
        ColorAlertConditionsMetricaValor condicaoAlertaCores;
        for (ColorAlertMetadata colorAlert : alerts) {
            propriedadeAlerta = ColorAlertProperties.factory(colorAlert.getFontColor(), colorAlert.getBackGroundColor(), colorAlert.getFontStyle(),
                    colorAlert.isBold(), colorAlert.isItalic(), colorAlert.getFontSize());
            propriedadeAlerta.setAlignment(ColorAlertProperties.ALIGNMENT_RIGHT);
            condicaoAlertaCores = new ColorAlertConditionsMetricaValor(colorAlert.getSequence(), propriedadeAlerta, colorAlert.getFunction(),
                    colorAlert.getAction(), colorAlert.getOperator(), metricMetaData, colorAlert.getValues());
            metricMetaData.addColorAlert(condicaoAlertaCores);
        }
    }

    public List<ColorAlertConditionsMetrica> getColorsAlertLines() {
        return getColorAlerts(this.lineColorAlertConditions);
    }

    public List<ColorAlertConditionsMetrica> getColorAlertCells() {
        return getColorAlerts(this.cellColorAlertConditions);
    }

    public List<ColorAlertConditionsMetrica> getColorsAlertLines(String function) {
        return lineColorAlertConditions.get(function);
    }

    public List<ColorAlertConditionsMetrica> getColorAlertCells(String function) {
        return cellColorAlertConditions.get(function);
    }

    protected void setTotalLinesType(String total) {
        if (MetaDataField.TOTAL_APPLY_SUM.equals(total)) {
            this.partialTotalizationApplyType = PartialTotalizationApplyTypeSoma.getInstance();
        } else {
            this.partialTotalizationApplyType = PartialTotalizationApplyTypeExpressao.getInstance();
        }
    }

    public int getDecimalPlacesNumber() {
        return this.decimalMaskRender.getNCasasDecimais();
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
        this.decimalMaskRender = new DecimalMaskRender(this.decimalPlaces);
    }

    @Override
    public String getTextNullValue() {
        return this.nullValueMask.getText();
    }

    public void addMetricOrdering(MetricOrdering metricOrdering) {
        this.metricOrderings.add(metricOrdering);
    }

    public String getFormattedValue(Object valor) {
        if (valor != null) {
            return super.getFormattedValue(this.decimalMaskRender.apply(valor));
        } else {
            return this.getTextNullValue();
        }
    }

    private List<ColorAlertConditionsMetrica> getColorAlerts(Map<String, List<ColorAlertConditionsMetrica>> alerts) {
        List<ColorAlertConditionsMetrica> result = new ArrayList<>();
        for (List<ColorAlertConditionsMetrica> colorAlertConditionsMetricas : alerts.values()) {
            result.addAll(colorAlertConditionsMetricas);
        }
        return result;
    }

    private void addColorAlertLine(ColorAlertConditionsMetrica colorAlert) {
        addColorAlert(colorAlert, this.lineColorAlertConditions);
    }

    private void addColorAlert(ColorAlertConditionsMetrica alertaCor, Map<String, List<ColorAlertConditionsMetrica>> alertMap) {
        String function = alertaCor.getFunction();
        List<ColorAlertConditionsMetrica> alerts = alertMap.get(function);
        if (alerts == null) {
            alerts = new ArrayList<>();
        }
        alerts.add(alertaCor);
        alertMap.put(function, alerts);
    }

    private void addColorAlertCell(ColorAlertConditionsMetrica colorAlert) {
        addColorAlert(colorAlert, this.cellColorAlertConditions);
    }

    public void addColorAlert(ColorAlertConditionsMetrica colorAlert) {
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

    public Double calculaValorTotalParcial(Dimension dimensionEixoReferencia, Dimension dimension) {
        return this.partialTotalizationApplyType.calculateValue(dimensionEixoReferencia, dimension, this, this.cube.getMetricsMap());
    }

    @Override
    public void printFieldTypeValue(Object valor, String cellProperty, Printer printer) {
        printer.printMetricValue(cellProperty, ((Double) valor), this);
    }

    public abstract Metric createMetrica();

}
