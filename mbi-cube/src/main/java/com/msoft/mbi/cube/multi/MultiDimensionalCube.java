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
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetricaOutroCampo;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertProperties;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionLineOutros;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaDataOthers;
import com.msoft.mbi.cube.multi.dimension.Dimensions;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoMetricaComparator;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFilters;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFiltersAccumulatedValue;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;
import com.msoft.mbi.cube.multi.metaData.CubeMetaData;
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
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAVParticipationMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoMetaData;

public class MultiDimensionalCube extends Cube {

    public MultiDimensionalCube(CubeMetaData metaData) {
        super(metaData);
    }

    private void factoryColorsAlertOtherFields(MetricMetaData metricMetaData, List<ColorAlertMetadata> alerts, boolean relatedFieldFunction) {
        ColorAlertProperties propriedadeAlerta;
        ColorAlertConditionsMetricaOutroCampo condicaoAlertaCores;
        for (ColorAlertMetadata colorAlert : alerts) {
            propriedadeAlerta = ColorAlertProperties.factory(colorAlert.getFontColor(), colorAlert.getBackGroundColor(), colorAlert.getFontStyle(), colorAlert.isBold(), colorAlert.isItalic(), colorAlert.getFontSize());
            propriedadeAlerta.setAlignment(ColorAlertProperties.ALIGNMENT_RIGHT);
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
        List<MetaDataField> camposMetrica = new ArrayList<>();
        for (MetaDataField campoDimensao : cubeMetaData.getDimensionFields()) {
            if ("S".equals(campoDimensao.getDefaultField())) {
                dimensao = DimensionMetaData.factory(campoDimensao);
                if (MetaDataField.LINE == campoDimensao.getDisplayLocation()) {
                    this.addHierarchyLine(dimensao);
                } else {
                    this.addHierarchyColumn(dimensao);
                }
                this.columnsViewed.add(dimensao);
            }
        }
        AnalysisParticipationType tipoAnaliseVerticalCampo = AnaliseParticipacaoTipoGeral.getInstance();
        AnalysisParticipationType tipoAnaliseHorizontalCampo = AnaliseParticipacaoTipoGeral.getInstance();
        for (MetaDataField campo : cubeMetaData.getMetricFields()) {
            camposMetrica.add(campo);
            String visualizacaoMetrica = this.getMetricVisualizationStatus(campo);
            if (!MetaDataField.NOT_ADDED_METRIC.equals(visualizacaoMetrica)) {
                MetricMetaData metaDataCampoOriginal;
                if (!campo.isExpression()) {
                    metaDataCampoOriginal = MetricAdditiveMetaData.factory(campo);
                    this.addHierarchyLineMetricAdditive((MetricAdditiveMetaData) metaDataCampoOriginal);
                } else {
                    campo.setName(this.convertConditionalExpression(campo.getName()));

                    metaDataCampoOriginal = MetricCalculatedMetaData.factory(campo);

                    Double valorExpr = null;
                    Double resultExpr = null;

                    try {
                        resultExpr = ((MetricCalculatedMetaData) metaDataCampoOriginal).createCalculo().calculateValue();
                        valorExpr = Double.valueOf(campo.getName());
                    } catch (Exception e) {
                    }

                    if (resultExpr != null && resultExpr.equals(valorExpr)) {
                        metaDataCampoOriginal = MetricAdditiveMetaData.factory(campo);
                        this.addHierarchyLineMetricAdditive((MetricAdditiveMetaData) metaDataCampoOriginal);
                    } else {
                        this.addHierarchyLineMetricCalculated((MetricCalculatedMetaData) metaDataCampoOriginal);
                    }
                }
                this.columnsViewed.add(metaDataCampoOriginal);

                if (MetaDataField.METRIC_RESTRICTED_VIEW.equals(visualizacaoMetrica)) {
                    metaDataCampoOriginal.setViewed(false);
                }

                if (campo.isTotalLineField()) {
                    if (metaDataCampoOriginal.isViewed()) {
                        this.metricsTotalHorizontal.add(metaDataCampoOriginal);
                    }
                }

                if (campo.hasVerticalAnalysis()) {
                    tipoAnaliseVerticalCampo = this.getAVType(campo.getVerticalAnalysisType());
                    List<ColorAlertMetadata> alertsAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAVParticipationMetaData.AV);
                    MetricCalculatedAVParticipationMetaData metricAV = new MetricCalculatedAVParticipationMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertsAV);
                    metricAV.setTotalLines(false);
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(metricAV);
                        this.columnsViewed.add(metricAV);
                    }
                }
                if (campo.hasAccumulatedValue()) {
                    List<ColorAlertMetadata> alertasAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAccValorAVMetaData.ACCUMULATED_VALUE_AV);
                    MetricCalculatedAccValorAVMetaData metricaAcumuladoValorAV = new MetricCalculatedAccValorAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertasAV);
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(metricaAcumuladoValorAV);
                        this.columnsViewed.add(metricaAcumuladoValorAV);
                    }
                }
                if (campo.hasAccumulatedParticipation()) {
                    MetricCalculatedParticipacaoMetaData metaDataAVAuxiliar;
                    if (!campo.hasVerticalAnalysis()) {
                        metaDataAVAuxiliar = new MetricCalculatedAVParticipationMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, null);
                        metaDataAVAuxiliar.setViewed(false);
                        if (metaDataCampoOriginal.isViewed()) {
                            this.addHierarchyLineMetricCalculated(metaDataAVAuxiliar);
                        }
                    } else {
                        metaDataAVAuxiliar = (MetricCalculatedParticipacaoMetaData) this.getMetricMetaDataRelativeField(campo.getTitle(), MetricCalculatedAVParticipationMetaData.AV);
                    }
                    List<ColorAlertMetadata> alertasAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAccParticipationAVMetaData.ACC_PARTICIPATION_AV);
                    if (metaDataCampoOriginal.isViewed()) {
                        MetricCalculatedAccParticipationAVMetaData metricaPartAcumAV = new MetricCalculatedAccParticipationAVMetaData(metaDataAVAuxiliar, tipoAnaliseVerticalCampo, alertasAV);
                        this.addHierarchyLineMetricCalculated(metricaPartAcumAV);
                        this.columnsViewed.add(metricaPartAcumAV);
                    }
                }

                if (campo.hasHorizontalAnalysis()) {
                    MetricCalculatedMetaData metaData;
                    List<ColorAlertMetadata> alertasAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAHEvolutionMetaData.AH);
                    if (MetaDataField.AH_TYPE_DYNAMIC.equals(campo.getHorizontalAnalysisType())) {
                        metaData = new MetricCalculatedAHEvolutionMetaData(metaDataCampoOriginal, alertasAV, AnaliseEvolucaoTipoDinamica.getInstance());
                    } else {
                        metaData = new MetricCalculatedAHEvolutionMetaData(metaDataCampoOriginal, alertasAV, AnaliseEvolucaoTipoFixa.getInstance());
                    }
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(metaData);
                        this.columnsViewed.add(metaData);
                    }
                }
                if (campo.hasHorizontalParticipation()) {
                    List<ColorAlertMetadata> alertasAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedParticipacaoAHMetaData.PARTICIPACAO_AH);
                    MetricCalculatedParticipacaoAHMetaData metricaPartAH = new MetricCalculatedParticipacaoAHMetaData(metaDataCampoOriginal, tipoAnaliseHorizontalCampo, alertasAV);
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(metricaPartAH);
                        this.columnsViewed.add(metricaPartAH);
                    }
                }
                if (campo.hasHorizontalAccumulatedParticipation()) {
                    MetricCalculatedParticipacaoAHMetaData metaDataAHAuxiliar;
                    if (!campo.hasHorizontalParticipation()) {
                        metaDataAHAuxiliar = new MetricCalculatedParticipacaoAHMetaData(metaDataCampoOriginal, tipoAnaliseHorizontalCampo, null);
                        metaDataAHAuxiliar.setViewed(false);
                        this.addHierarchyLineMetricCalculated(metaDataAHAuxiliar);
                    } else {
                        metaDataAHAuxiliar = (MetricCalculatedParticipacaoAHMetaData) this.getMetricMetaDataRelativeField(campo.getTitle(), MetricCalculatedParticipacaoAHMetaData.PARTICIPACAO_AH);
                    }
                    List<ColorAlertMetadata> alertasAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAccParticipationAHMetaData.ACC_PARTICIPATION_AH);
                    if (metaDataCampoOriginal.isViewed()) {
                        MetricCalculatedAccParticipationAHMetaData metricaPartAcumAH = new MetricCalculatedAccParticipationAHMetaData(metaDataAHAuxiliar, tipoAnaliseHorizontalCampo, alertasAV);
                        this.addHierarchyLineMetricCalculated(metricaPartAcumAH);
                        this.columnsViewed.add(metricaPartAcumAH);
                    }
                }
            }
        }

        List<MetricCalculatedMetaData> metricasCalculadas = this.getHierarchyMetricCalculated();
        CalculationHierarchyOrdering hierarquiaCalculo = new CalculationHierarchyOrdering(metricasCalculadas);
        metricasCalculadas = hierarquiaCalculo.getCalculatedMetricsOrdered();
        this.setHierarchyMetricCalculated(metricasCalculadas);

        for (MetaDataField campo : camposMetrica) {
            if (!campo.getColorAlertMetadataSecondField().isEmpty()) {
                MetricMetaData metaData = this.getMetricByTitle(campo.getTitle());
                if (metaData != null) {
                    this.factoryColorsAlertOtherFields(metaData, campo.getColorAlertMetadataSecondField(), false);
                }
            }
            if (campo.hasVerticalAnalysis()) {
                List<ColorAlertMetadata> alertasAV = campo.getRelativeSecondFieldFunctionValueAlert(MetricCalculatedAVParticipationMetaData.AV);
                if (alertasAV != null) {
                    MetricMetaData campoFuncaoReferencia = this.getMetricMetaDataRelativeField(campo.getTitle(), MetricCalculatedAVParticipationMetaData.AV);
                    if (campoFuncaoReferencia != null) {
                        factoryColorsAlertOtherFields(campoFuncaoReferencia, alertasAV, true);
                    }
                }
            }
        }

        if (cubeMetaData.getMetricFieldsExpression() != null && !cubeMetaData.getMetricFieldsExpression().trim().isEmpty()) {
            String expressaoFiltro = this.convertMetricFilterToFieldTitle(cubeMetaData.getMetricFieldsExpression());
            this.metricFilters = new MetricFilters(expressaoFiltro, MetricValueUseLine.getInstance());
        }
        if (cubeMetaData.getAccumulatedFieldExpression() != null && !cubeMetaData.getAccumulatedFieldExpression().trim().isEmpty()) {
            String expressaoFiltro = this.convertMetricFilterToFieldTitle(cubeMetaData.getAccumulatedFieldExpression());
            this.metricFiltersAccumulatedValue = new MetricFiltersAccumulatedValue(expressaoFiltro);
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
            this.getLastMetaDataLine().setComparator(new DimensaoMetricaComparator(this.metricsMap, this.getDimensionsLastLevelColumns(), metricOrderings));
            this.reorderLastLevelDimension(metricOrderings);
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

    protected void reorderLastLevelDimension(List<MetricOrdering> orderedMetrics) {
        List<Dimension> dimensionsLastLevelColumns = this.getDimensionsLastLevelColumns();
        List<Dimension> parentDimensionsReorder = new ArrayList<>();
        this.generateDimensionsChildReorderList(this, parentDimensionsReorder);
        List<Dimension> dimensions;
        for (Dimension parentDimension : parentDimensionsReorder) {
            dimensions = new ArrayList<>(parentDimension.getDimensionsLine().values());
            parentDimension.resetDimensionsLines(new DimensaoMetricaComparator(this.metricsMap, dimensionsLastLevelColumns, orderedMetrics));
            for (Dimension dimension : dimensions) {
                parentDimension.addDimensionLine(dimension);
                Dimension.increaseTotalSize(parentDimension);
            }
        }
    }

}
