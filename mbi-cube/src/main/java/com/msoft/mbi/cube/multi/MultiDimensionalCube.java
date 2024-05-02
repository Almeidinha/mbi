package com.msoft.mbi.cube.multi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnaliseEvolucaoTipoDinamica;
import com.msoft.mbi.cube.multi.analytics.AnaliseEvolucaoTipoFixa;
import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoGeral;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoParcialProxNivel;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoParcialProxNivelTotalizado;
import com.msoft.mbi.cube.multi.coloralertcondition.ColorAlertConditionsMetricaOutroCampo;
import com.msoft.mbi.cube.multi.coloralertcondition.ColorAlertProperties;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionLineOutros;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaDataOthers;
import com.msoft.mbi.cube.multi.dimension.Dimensions;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensionMetricComparator;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import com.msoft.mbi.cube.multi.resumefunctions.MetricFilters;
import com.msoft.mbi.cube.multi.resumefunctions.MetricFiltersAccumulatedValue;
import com.msoft.mbi.cube.multi.metadata.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metadata.MetaDataField;
import com.msoft.mbi.cube.multi.metadata.CubeMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricValueUseLine;
import com.msoft.mbi.cube.multi.metrics.MetricOrdering;
import com.msoft.mbi.cube.multi.metrics.additive.MetricAdditiveMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.CalculationHierarchyOrdering;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAccParticipationAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAccParticipationAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAccValorAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAHEvolutionMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipationAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAVParticipationMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipationMetaData;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MultiDimensionalCube extends Cube {

    public MultiDimensionalCube(CubeMetaData metaData) {
        super(metaData);
    }

    private void factoryColorsAlertOtherFields(MetricMetaData metricMetaData, List<ColorAlertMetadata> alerts, boolean relatedFieldFunction) {
        ColorAlertProperties propriedadeAlerta;
        ColorAlertConditionsMetricaOutroCampo condicaoAlertaCores;
        for (ColorAlertMetadata colorAlert : alerts) {
            propriedadeAlerta = ColorAlertProperties.factory(colorAlert.getFontColor(), colorAlert.getBackGroundColor(), colorAlert.getFontStyle(), colorAlert.isBold(), colorAlert.isItalic(), colorAlert.getFontSize());
            propriedadeAlerta.setAlignment(CellProperty.ALIGNMENT_RIGHT);
            MetricMetaData outroCampo;
            if (!relatedFieldFunction) {
                outroCampo = getMetricByTitle(colorAlert.getSecondFieldTitle());
            } else {
                outroCampo = getMetricMetaDataRelativeField(colorAlert.getSecondFieldTitle(), colorAlert.getDestineFieldFunction());
            }
            if (outroCampo != null) {
                condicaoAlertaCores = new ColorAlertConditionsMetricaOutroCampo(colorAlert.getSequence(), propriedadeAlerta, colorAlert.getFunction(), colorAlert.getAction(), colorAlert.getOperator(), metricMetaData, colorAlert.getTypeComparison(), colorAlert.getReferenceValue(), outroCampo, colorAlert.getDestineFieldFunction());
                metricMetaData.addColorAlert(condicaoAlertaCores);
            }
        }
    }

    private AnalysisParticipationType getAVType(String type) {
        if (MetaDataField.AV_TYPE_PARTIAL_NEXT_LEVEL.equals(type)) {
            return AnaliseParticipacaoTipoParcialProxNivel.getInstance();
        } else if (MetaDataField.AV_TYPE_PARTIAL_NEXT_LEVEL_TOTAL.equals(type)) {
            return AnaliseParticipacaoTipoParcialProxNivelTotalizado.getInstance();
        } else {
            return AnaliseParticipacaoTipoGeral.getInstance();
        }
    }

    @Override
    protected void factory() {
        CubeMetaData cubeMetaData = (CubeMetaData) this.metaData;
        cubeMetaData.orderFields();
        cubeMetaData.orderDimensionFields();
        cubeMetaData.orderMetricFields();
        DimensionMetaData dimensao;
        List<MetaDataField> metricFields = new ArrayList<>();
        for (MetaDataField dimensionField : cubeMetaData.getDimensionFields()) {
            if ("S".equals(dimensionField.getDefaultField())) {
                dimensao = DimensionMetaData.factory(dimensionField);
                if (MetaDataField.LINE == dimensionField.getDisplayLocation()) {
                    this.addHierarchyLine(dimensao);
                } else {
                    this.addHierarchyColumn(dimensao);
                }
                this.columnsViewed.add(dimensao);
            }
        }
        AnalysisParticipationType tipoAnaliseVerticalCampo = AnaliseParticipacaoTipoGeral.getInstance();
        AnalysisParticipationType participationType = AnaliseParticipacaoTipoGeral.getInstance();
        for (MetaDataField metaDataField : cubeMetaData.getMetricFields()) {
            metricFields.add(metaDataField);
            String metricVisualization = this.getMetricVisualizationStatus(metaDataField);
            if (!MetaDataField.NOT_ADDED_METRIC.equals(metricVisualization)) {
                MetricMetaData metaDataCampoOriginal;
                if (!metaDataField.isExpression()) {
                    metaDataCampoOriginal = MetricAdditiveMetaData.factory(metaDataField);
                    this.addHierarchyLineMetricAdditive((MetricAdditiveMetaData) metaDataCampoOriginal);
                } else {
                    metaDataField.setName(this.convertConditionalExpression(metaDataField.getName()));

                    metaDataCampoOriginal = MetricCalculatedMetaData.factory(metaDataField);

                    Double valorExpr = null;
                    Double resultExpr = null;

                    try {
                        resultExpr = ((MetricCalculatedMetaData) metaDataCampoOriginal).createCalculo().calculateValue();
                        valorExpr = Double.valueOf(metaDataField.getName());
                    } catch (Exception e) {
                        log.error("Erro ao calcular valor da expressão: " + metaDataField.getName(), e);
                    }

                    if (resultExpr != null && resultExpr.equals(valorExpr)) {
                        metaDataCampoOriginal = MetricAdditiveMetaData.factory(metaDataField);
                        this.addHierarchyLineMetricAdditive((MetricAdditiveMetaData) metaDataCampoOriginal);
                    } else {
                        this.addHierarchyLineMetricCalculated((MetricCalculatedMetaData) metaDataCampoOriginal);
                    }
                }
                this.columnsViewed.add(metaDataCampoOriginal);

                if (MetaDataField.METRIC_RESTRICTED_VIEW.equals(metricVisualization)) {
                    metaDataCampoOriginal.setViewed(false);
                }

                if (metaDataField.isTotalLineField() && metaDataCampoOriginal.isViewed()) {
                    this.metricsTotalHorizontal.add(metaDataCampoOriginal);
                }

                if (metaDataField.hasVerticalAnalysis()) {
                    tipoAnaliseVerticalCampo = this.getAVType(metaDataField.getVerticalAnalysisType());
                    List<ColorAlertMetadata> alertsAV = metaDataField.getRelativeFieldFunctionValueAlert(MetricCalculatedAVParticipationMetaData.AV);
                    MetricCalculatedAVParticipationMetaData metricAV = new MetricCalculatedAVParticipationMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertsAV);
                    metricAV.setTotalLines(false);
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(metricAV);
                        this.columnsViewed.add(metricAV);
                    }
                }
                if (metaDataField.hasAccumulatedValue()) {
                    List<ColorAlertMetadata> alertMetadata = metaDataField.getRelativeFieldFunctionValueAlert(MetricCalculatedAccValorAVMetaData.ACCUMULATED_VALUE_AV);
                    MetricCalculatedAccValorAVMetaData calculatedAccValorAVMetaData = new MetricCalculatedAccValorAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertMetadata);
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(calculatedAccValorAVMetaData);
                        this.columnsViewed.add(calculatedAccValorAVMetaData);
                    }
                }
                if (metaDataField.hasAccumulatedParticipation()) {
                    MetricCalculatedParticipationMetaData participationMetaData;
                    if (!metaDataField.hasVerticalAnalysis()) {
                        participationMetaData = new MetricCalculatedAVParticipationMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, null);
                        participationMetaData.setViewed(false);
                        if (metaDataCampoOriginal.isViewed()) {
                            this.addHierarchyLineMetricCalculated(participationMetaData);
                        }
                    } else {
                        participationMetaData = (MetricCalculatedParticipationMetaData) this.getMetricMetaDataRelativeField(metaDataField.getTitle(), MetricCalculatedAVParticipationMetaData.AV);
                    }
                    List<ColorAlertMetadata> alertasAV = metaDataField.getRelativeFieldFunctionValueAlert(MetricCalculatedAccParticipationAVMetaData.ACC_PARTICIPATION_AV);
                    if (metaDataCampoOriginal.isViewed()) {
                        MetricCalculatedAccParticipationAVMetaData metricaPartAcumAV = new MetricCalculatedAccParticipationAVMetaData(participationMetaData, tipoAnaliseVerticalCampo, alertasAV);
                        this.addHierarchyLineMetricCalculated(metricaPartAcumAV);
                        this.columnsViewed.add(metricaPartAcumAV);
                    }
                }

                if (metaDataField.hasHorizontalAnalysis()) {
                    MetricCalculatedMetaData metaData;
                    List<ColorAlertMetadata> alertasAV = metaDataField.getRelativeFieldFunctionValueAlert(MetricCalculatedAHEvolutionMetaData.AH);
                    if (MetaDataField.AH_TYPE_DYNAMIC.equals(metaDataField.getHorizontalAnalysisType())) {
                        metaData = new MetricCalculatedAHEvolutionMetaData(metaDataCampoOriginal, alertasAV, AnaliseEvolucaoTipoDinamica.getInstance());
                    } else {
                        metaData = new MetricCalculatedAHEvolutionMetaData(metaDataCampoOriginal, alertasAV, AnaliseEvolucaoTipoFixa.getInstance());
                    }
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(metaData);
                        this.columnsViewed.add(metaData);
                    }
                }
                if (metaDataField.hasHorizontalParticipation()) {
                    List<ColorAlertMetadata> alertMetadata = metaDataField.getRelativeFieldFunctionValueAlert(MetricCalculatedParticipationAHMetaData.PARTICIPACAO_AH);
                    MetricCalculatedParticipationAHMetaData ahMetaData = new MetricCalculatedParticipationAHMetaData(metaDataCampoOriginal, participationType, alertMetadata);
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(ahMetaData);
                        this.columnsViewed.add(ahMetaData);
                    }
                }
                if (metaDataField.hasHorizontalAccumulatedParticipation()) {
                    MetricCalculatedParticipationAHMetaData ahMetaData;
                    if (!metaDataField.hasHorizontalParticipation()) {
                        ahMetaData = new MetricCalculatedParticipationAHMetaData(metaDataCampoOriginal, participationType, null);
                        ahMetaData.setViewed(false);
                        this.addHierarchyLineMetricCalculated(ahMetaData);
                    } else {
                        ahMetaData = (MetricCalculatedParticipationAHMetaData) this.getMetricMetaDataRelativeField(metaDataField.getTitle(), MetricCalculatedParticipationAHMetaData.PARTICIPACAO_AH);
                    }
                    List<ColorAlertMetadata> alertMetadata = metaDataField.getRelativeFieldFunctionValueAlert(MetricCalculatedAccParticipationAHMetaData.ACC_PARTICIPATION_AH);
                    if (metaDataCampoOriginal.isViewed()) {
                        MetricCalculatedAccParticipationAHMetaData metricPartAccumAH = new MetricCalculatedAccParticipationAHMetaData(ahMetaData, participationType, alertMetadata);
                        this.addHierarchyLineMetricCalculated(metricPartAccumAH);
                        this.columnsViewed.add(metricPartAccumAH);
                    }
                }
            }
        }

        List<MetricCalculatedMetaData> metricasCalculadas = this.getHierarchyMetricCalculated();
        CalculationHierarchyOrdering hierarchyOrdering = new CalculationHierarchyOrdering(metricasCalculadas);
        metricasCalculadas = hierarchyOrdering.getCalculatedMetricsOrdered();
        this.setHierarchyMetricCalculated(metricasCalculadas);

        for (MetaDataField metaDataField : metricFields) {
            if (!metaDataField.getColorAlertMetadataSecondField().isEmpty()) {
                MetricMetaData metaData = this.getMetricByTitle(metaDataField.getTitle());
                if (metaData != null) {
                    this.factoryColorsAlertOtherFields(metaData, metaDataField.getColorAlertMetadataSecondField(), false);
                }
            }
            if (metaDataField.hasVerticalAnalysis()) {
                List<ColorAlertMetadata> valueAlert = metaDataField.getRelativeSecondFieldFunctionValueAlert(MetricCalculatedAVParticipationMetaData.AV);
                if (valueAlert != null) {
                    MetricMetaData metaDataRelativeField = this.getMetricMetaDataRelativeField(metaDataField.getTitle(), MetricCalculatedAVParticipationMetaData.AV);
                    if (metaDataRelativeField != null) {
                        factoryColorsAlertOtherFields(metaDataRelativeField, valueAlert, true);
                    }
                }
            }
        }

        if (cubeMetaData.getMetricFieldsExpression() != null && !cubeMetaData.getMetricFieldsExpression().trim().isEmpty()) {
            String filterExpression = this.convertMetricFilterToFieldTitle(cubeMetaData.getMetricFieldsExpression());
            this.metricFilters = new MetricFilters(filterExpression, MetricValueUseLine.getInstance());
        }
        if (cubeMetaData.getAccumulatedFieldExpression() != null && !cubeMetaData.getAccumulatedFieldExpression().trim().isEmpty()) {
            String filterExpression = this.convertMetricFilterToFieldTitle(cubeMetaData.getAccumulatedFieldExpression());
            this.metricFiltersAccumulatedValue = new MetricFiltersAccumulatedValue(filterExpression);
        }

    }

    @Override
    protected void updateSequenceRanking(Collection<Dimension> dimensions) {
        int sequence = 1;
        for (Dimension dimension : dimensions) {
            updateSequenceRanking(dimension.getDimensionsLine().values());
            dimension.setRankingSequence(sequence++);
        }
    }

    private void applyRankings(Dimensions dimensions, Dimension dimensionPai) {
        Dimension primeiraDimension = (Dimension) dimensions.firstKey();
        if (primeiraDimension.getMetaData().hasSequenceFields() && primeiraDimension.getMetaData().getFunctionRanking() != null) {
            this.applyRanking((List<Dimension>) dimensions.values(), primeiraDimension.getMetaData().getFunctionRanking(), dimensions.size());
        }
        for (Dimension dimension : dimensionPai.getDimensionsLine().values()) {
            if (!dimension.getDimensionsLine().isEmpty()) {
                this.applyRankings(dimension.getDimensionsLine(), dimension);
            }
        }
    }

    @Override
    protected void verifyRanking() {
        this.applyRankings(this.dimensionsLine, this);
    }

    @Override
    protected void removeDimensionsFiltersFunction(Dimension dimensionAddOthers, List<Dimension> dimensions) {
        List<Dimension> dimensionsLastLevelLines = this.getDimensionsLastLevelLines();
        DimensionMetaDataOthers metaDataOutros = new DimensionMetaDataOthers(dimensionAddOthers.getMetaData().getChild());
        DimensionLineOutros dimensionOthers = new DimensionLineOutros(dimensionAddOthers, metaDataOutros);
        for (Dimension dimensionLineToRemove : dimensions) {
            dimensionOthers.process(dimensionLineToRemove);
            dimensionLineToRemove.getParent().removeDimensionLine(dimensionLineToRemove);
            this.metricsMap.removeMetricLine(dimensionLineToRemove);
            dimensionsLastLevelLines.remove(dimensionLineToRemove);
        }
    }

    @Override
    protected void reorderData(List<MetricOrdering> metricOrderings) {
        if (!metricOrderings.isEmpty()) {
            this.getLastMetaDataLine().setComparator(new DimensionMetricComparator(this.metricsMap, metricOrderings));
            this.reorderLastLevelDimension();
        }
    }

    protected void generateDimensionsChildReorderList(Dimension parentDimension, List<Dimension> parentDimensions) {
        for (Dimension dimension : parentDimension.getDimensionsLine().values()) {
            if (dimension.getMetaData().isLast()) {
                parentDimensions.add(parentDimension);
                break;
            } else {
                generateDimensionsChildReorderList(dimension, parentDimensions);
            }
        }
    }

    protected void addMetricOrdering(List<MetricOrdering> metricOrderings, List<MetricOrdering> orderingList) {
        for (MetricOrdering metricOrdering : metricOrderings) {
            if (metricOrdering.getOrderingSequence() > this.getLastMetaDataLine().getOrderingSequence()) {
                orderingList.add(metricOrdering);
            }
        }
    }

    protected void reorderLastLevelDimension() {
        List<Dimension> parentDimensionsReorder = new ArrayList<>();
        this.generateDimensionsChildReorderList(this, parentDimensionsReorder);
        List<Dimension> dimensions;
        for (Dimension parentDimension : parentDimensionsReorder) {
            dimensions = new ArrayList<>(parentDimension.getDimensionsLine().values());
            parentDimension.resetDimensionsLines();
            for (Dimension dimension : dimensions) {
                parentDimension.addDimensionLine(dimension);
                Dimension.increaseTotalSize(parentDimension);
            }
        }
    }

}
