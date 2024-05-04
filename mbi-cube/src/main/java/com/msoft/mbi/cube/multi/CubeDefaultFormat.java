package com.msoft.mbi.cube.multi;

import java.util.*;

import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationTypeGeneral;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaDataAuxiliary;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensionMetricComparator;
import com.msoft.mbi.cube.multi.resumefunctions.MetricFilters;
import com.msoft.mbi.cube.multi.resumefunctions.MetricFiltersAccumulatedValue;
import com.msoft.mbi.cube.multi.metadata.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metadata.MetaDataField;
import com.msoft.mbi.cube.multi.metadata.CubeMetaData;
import com.msoft.mbi.cube.multi.metadata.FieldOrderingComparator;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricValueUseLine;
import com.msoft.mbi.cube.multi.metrics.MetricOrdering;
import com.msoft.mbi.cube.multi.metrics.additive.MetricAdditiveMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.CalculationHierarchyOrdering;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAccParticipationAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAccValorAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAVParticipationMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipationMetaData;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CubeDefaultFormat extends Cube {

    private DimensionMetaData lastOrderedDimension;

    protected CubeDefaultFormat(CubeMetaData metaData) {
        super(metaData);
    }

    @Override
    protected void factory() {
        CubeMetaData cubeMetaData = (CubeMetaData) this.metaData;
        cubeMetaData.orderFields();
        cubeMetaData.orderDimensionFields();

        Map<MetaDataField, DimensionMetaData> dimensions = new HashMap<>();

        List<MetaDataField> dimensionsByOrder = new ArrayList<>(cubeMetaData.getDimensionFields());
        FieldOrderingComparator comparator = new FieldOrderingComparator();
        dimensionsByOrder.sort(comparator);
        DimensionMetaDataAuxiliary metaDataAuxiliary = new DimensionMetaDataAuxiliary();
        this.addHierarchyLine(metaDataAuxiliary);
        for (MetaDataField metaDataField : dimensionsByOrder) {
            if ("S".equals(metaDataField.getDefaultField())) {
                DimensionMetaData dimensionMetaData = DimensionMetaData.factory(metaDataField);
                this.addHierarchyLine(dimensionMetaData);
                dimensions.put(metaDataField, dimensionMetaData);
                if (metaDataField.getOrder() > 0) {
                    this.lastOrderedDimension = dimensionMetaData;
                }
            }
        }

        for (MetaDataField campo : cubeMetaData.getFields()) {
            if (MetaDataField.DIMENSION.equals(campo.getFieldType())) {
                if ("S".equals(campo.getDefaultField())) {
                    this.columnsViewed.add(dimensions.get(campo));
                }
            } else {
                AnalysisParticipationType participacaoTipoGeral = AnalysisParticipationTypeGeneral.getInstance();
                String visualizationStatus = this.getMetricVisualizationStatus(campo);
                if (!MetaDataField.NOT_ADDED_METRIC.equals(visualizationStatus)) {
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
                            resultExpr = ((MetricCalculatedMetaData) metaDataCampoOriginal).createCalculation().calculateValue();
                            valorExpr = Double.valueOf(campo.getName());
                        } catch (Exception e) {
                            log.error("Error: " + e.getMessage());
                        }

                        if (resultExpr != null && resultExpr.equals(valorExpr)) {
                            metaDataCampoOriginal = MetricAdditiveMetaData.factory(campo);
                            this.addHierarchyLineMetricAdditive((MetricAdditiveMetaData) metaDataCampoOriginal);
                        } else {
                            this.addHierarchyLineMetricCalculated((MetricCalculatedMetaData) metaDataCampoOriginal);
                        }
                    }

                    if (MetaDataField.METRIC_RESTRICTED_VIEW.equals(visualizationStatus)) {
                        metaDataCampoOriginal.setViewed(false);
                    } else {
                        this.columnsViewed.add(metaDataCampoOriginal);
                    }

                    if (campo.hasVerticalAnalysis()) {
                        List<ColorAlertMetadata> alertsAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAVParticipationMetaData.AV);
                        MetricCalculatedAVParticipationMetaData metricAV = new MetricCalculatedAVParticipationMetaData(metaDataCampoOriginal, participacaoTipoGeral, alertsAV);
                        metricAV.setTotalLines(true);
                        this.addHierarchyLineMetricCalculated(metricAV);
                        this.columnsViewed.add(metricAV);
                    }
                    if (campo.hasAccumulatedValue()) {
                        List<ColorAlertMetadata> alertsAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAccValorAVMetaData.ACCUMULATED_VALUE_AV);
                        MetricCalculatedAccValorAVMetaData metricAccumValueAV = new MetricCalculatedAccValorAVMetaData(metaDataCampoOriginal, participacaoTipoGeral, alertsAV);
                        this.addHierarchyLineMetricCalculated(metricAccumValueAV);
                        this.columnsViewed.add(metricAccumValueAV);
                    }
                    if (campo.hasAccumulatedParticipation()) {
                        MetricCalculatedParticipationMetaData metaDataAVAuxiliar;
                        if (!campo.hasVerticalAnalysis()) {
                            metaDataAVAuxiliar = new MetricCalculatedAVParticipationMetaData(metaDataCampoOriginal, participacaoTipoGeral, null);
                            metaDataAVAuxiliar.setViewed(false);
                            this.addHierarchyLineMetricCalculated(metaDataAVAuxiliar);
                        } else {
                            metaDataAVAuxiliar = (MetricCalculatedParticipationMetaData) this.getMetricMetaDataRelativeField(campo.getTitle(), MetricCalculatedAVParticipationMetaData.AV);
                        }
                        List<ColorAlertMetadata> alertsAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAccParticipationAVMetaData.ACC_PARTICIPATION_AV);
                        MetricCalculatedAccParticipationAVMetaData metricPartAccumAV = new MetricCalculatedAccParticipationAVMetaData(metaDataAVAuxiliar, participacaoTipoGeral, alertsAV);
                        this.addHierarchyLineMetricCalculated(metricPartAccumAV);
                        this.columnsViewed.add(metricPartAccumAV);
                    }
                }
            }
        }

        List<MetricCalculatedMetaData> metricasCalculadas = this.getHierarchyMetricCalculated();
        CalculationHierarchyOrdering hierarchyCalculate = new CalculationHierarchyOrdering(metricasCalculadas);
        metricasCalculadas = hierarchyCalculate.getCalculatedMetricsOrdered();
        this.setHierarchyMetricCalculated(metricasCalculadas);

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
    public void updateSequenceRanking(Collection<Dimension> dimensions) {
        List<Dimension> lastLevelDimension = this.getDimensionsLastLevelLines();
        int sequencia = 1;
        for (Dimension lastLine : lastLevelDimension) {
            lastLine.setRankingSequence(sequencia++);
        }
    }

    private void applyRanking(List<Dimension> dimensions) {
        if (!dimensions.isEmpty()) {
            ColumnMetaData firstColumn = this.getColumnsViewed().get(0);
            if (firstColumn.hasSequenceFields() && firstColumn.getFunctionRanking() != null) {
                this.applyRanking(dimensions, firstColumn.getFunctionRanking(), dimensions.size());
            }
        }
    }

    @Override
    protected void verifyRanking() {
        this.applyRanking(this.getDimensionsLastLevelLines());
    }

    @Override
    protected void removeDimensionsFiltersFunction(Dimension dimensionAddOthers, List<Dimension> dimensions) {
        List<Dimension> lastLevelDimension = this.getDimensionsLastLevelLines();
        for (Dimension dimension : dimensions) {
            Dimension dimensionPai = dimension.getParent();
            dimensionPai.removeDimensionLine(dimension);
            this.metricsMap.removeMetricLine(dimension);
            lastLevelDimension.remove(dimension);
        }
    }

    @Override
    protected void reorderData(List<MetricOrdering> metricOrderings) {
        if (!metricOrderings.isEmpty()) {
            if (this.lastOrderedDimension == null) {
                this.getDimensionsLastLevelLines().sort(new DimensionMetricComparator(this.metricsMap, metricOrderings));
            } else {
                List<Dimension> parentDimension = new ArrayList<>();
                this.buildDimensionReorderChildrenList(this, parentDimension);
                for (Dimension dimension : parentDimension) {
                    List<Dimension> dimensionLine = new ArrayList<>();
                    this.getLastLevelList(dimension.getDimensionsLine().values(), dimensionLine);
                    dimensionLine.sort(new DimensionMetricComparator(this.metricsMap, metricOrderings));
                }
            }
        }
    }

    private void buildDimensionReorderChildrenList(Dimension dimensionPai, List<Dimension> parentDimension) {
        for (Dimension dimension : dimensionPai.getDimensionsLine().values()) {
            if (dimension.getMetaData().equals(this.lastOrderedDimension)) {
                parentDimension.add(dimension);
            } else {
                buildDimensionReorderChildrenList(dimension, parentDimension);
                break;
            }
        }
    }
    @Override
    protected void addMetricOrdering(List<MetricOrdering> metricOrderings, List<MetricOrdering> orderingList) {
        orderingList.addAll(metricOrderings);
    }

}
