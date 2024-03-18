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
import com.msoft.mbi.cube.multi.generation.Impressor;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
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
    public static final String EMPTY_AGGREGATION = "VAZIO";
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
        super(title, CampoMetaData.METRICA);
        this.type = type;
        this.cellColorAlertConditions = new HashMap<>();
        this.lineColorAlertConditions = new HashMap<>();
        this.nullValueMask = new NullValueMask();
        this.decimalMaskRender = new DecimalMaskRender();
        this.partialTotalizationApplyType = PartialTotalizationApplyTypeSoma.getInstance();
        this.metricOrderings = new ArrayList<>();
        this.aggregationType = SUM_AGGREGATION;
    }

    public static void factory(MetricMetaData metricMetaData, CampoMetaData campoMetaData) {
        ColumnMetaData.factory(metricMetaData, campoMetaData);
        if (campoMetaData.getOrdem() > 0) {
            MetricOrdering metricOrdering = new MetricOrdering(campoMetaData.getSentidoOrdem(), campoMetaData.getOrdem(),
                    metricMetaData.getTitle(), MetricaValorUtilizarLinhaMetrica.getInstance());
            metricMetaData.addMetricOrdering(metricOrdering);
        }
        if (campoMetaData.getOrdemAcumulado() > 0) {
            MetricOrdering metricOrdering = new MetricOrdering(campoMetaData.getSentidoOrdemAcumulado(), campoMetaData.getOrdemAcumulado(),
                    metricMetaData.getTitle(), MetricaValorUtilizarTotal.getInstance());
            metricMetaData.addMetricOrdering(metricOrdering);
        }
        if (campoMetaData.getMascaraValorNulo() != null) {
            metricMetaData.setNullValueMask(new NullValueMask(campoMetaData.getMascaraValorNulo()));
        }
        factoryColorsAlert(metricMetaData, campoMetaData.getAlertasCoresValor());

        metricMetaData.setDecimalPlaces(campoMetaData.getNumPosDecimais());
        metricMetaData.setTotalSumColumns(campoMetaData.isAcumulaCampoLinha());
        metricMetaData.setTotalSumGeneralColumns(campoMetaData.isTotalizaCampoLinha());
        metricMetaData.setTotalLines(campoMetaData.isTotalizaCampo());
        metricMetaData.setTotalPartialLines(campoMetaData.isTotalizacaoParcial());
        metricMetaData.setTotalPartialColumns(campoMetaData.isTotalizacaoParcial());

        metricMetaData.setMediaPartialColumns(campoMetaData.isMediaParcial());
        metricMetaData.setMediaPartialLines(campoMetaData.isMediaParcial());
        metricMetaData.setExpressionPartialColumns(campoMetaData.isExpressaoNaParcial());
        metricMetaData.setExpressionPartialLines(campoMetaData.isExpressaoNaParcial());
        metricMetaData.setPartialTotalExpression(campoMetaData.isExpressaoNaParcialTotal());
        metricMetaData.setTotalMediaColumns(campoMetaData.isUtilizaMediaLinha());
        metricMetaData.setAggregationType(campoMetaData.getAgregacaoTipo());
    }

    protected static void factoryColorsAlert(MetricMetaData metricMetaData, List<AlertaCorMetaData> alerts) {
        ColorAlertProperties propriedadeAlerta;
        ColorAlertConditionsMetricaValor condicaoAlertaCores;
        for (AlertaCorMetaData colorAlert : alerts) {
            propriedadeAlerta = ColorAlertProperties.factory(colorAlert.getCorFonte(), colorAlert.getCorFundo(), colorAlert.getEstiloFonte(),
                    colorAlert.isNegrito(), colorAlert.isItalico(), colorAlert.getTamanhoFonte());
            propriedadeAlerta.setAlignment(ColorAlertProperties.ALINHAMENTO_DIREITA);
            condicaoAlertaCores = new ColorAlertConditionsMetricaValor(colorAlert.getSequencia(), propriedadeAlerta, colorAlert.getFuncao(),
                    colorAlert.getAcao(), colorAlert.getOperador(), metricMetaData, colorAlert.getValores());
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
        if (CampoMetaData.TOTALIZAR_APLICAR_SOMA.equals(total)) {
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
        if (AlertaCorMetaData.ACAO_PINTAR_LINHA == colorAlert.getAction()) {
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
    public void printFieldTypeValue(Object valor, String cellProperty, Impressor impressor) {
        impressor.imprimeValorMetrica(cellProperty, ((Double) valor), this);
    }

    public abstract Metric createMetrica();

}
