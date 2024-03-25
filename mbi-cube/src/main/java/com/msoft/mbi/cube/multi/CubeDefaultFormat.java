package com.msoft.mbi.cube.multi;

import java.util.*;

import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoGeral;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaDataAuxiliary;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoMetricaComparator;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFilters;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFiltersAccumulatedValue;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;
import com.msoft.mbi.cube.multi.metaData.CubeMetaData;
import com.msoft.mbi.cube.multi.metaData.FieldOrderingComparator;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricValueUseLine;
import com.msoft.mbi.cube.multi.metrics.MetricOrdering;
import com.msoft.mbi.cube.multi.metrics.additive.MetricAdditiveMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.CalculationHierarchyOrdering;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAccParticipationAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAccValorAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAVParticipationMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoMetaData;
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
        for (MetaDataField campo : dimensionsByOrder) {
            if ("S".equals(campo.getDefaultField())) {
                DimensionMetaData dimensionMetaData = DimensionMetaData.factory(campo);
                this.addHierarchyLine(dimensionMetaData);
                dimensions.put(campo, dimensionMetaData);
                if (campo.getOrder() > 0) {
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
                AnalysisParticipationType tipoAnaliseVerticalCampo = AnaliseParticipacaoTipoGeral.getInstance();
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
                            log.error("Error: " + e.getMessage());
                        }

                        if (resultExpr != null && resultExpr.equals(valorExpr)) {
                            metaDataCampoOriginal = MetricAdditiveMetaData.factory(campo);
                            this.addHierarchyLineMetricAdditive((MetricAdditiveMetaData) metaDataCampoOriginal);
                        } else {
                            this.addHierarchyLineMetricCalculated((MetricCalculatedMetaData) metaDataCampoOriginal);
                        }
                    }

                    if (MetaDataField.METRIC_RESTRICTED_VIEW.equals(visualizacaoMetrica)) {
                        metaDataCampoOriginal.setViewed(false);
                    } else {
                        this.columnsViewed.add(metaDataCampoOriginal);
                    }

                    if (campo.hasVerticalAnalysis()) {
                        List<ColorAlertMetadata> alertsAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAVParticipationMetaData.AV);
                        MetricCalculatedAVParticipationMetaData metricAV = new MetricCalculatedAVParticipationMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertsAV);
                        metricAV.setTotalLines(true);
                        this.addHierarchyLineMetricCalculated(metricAV);
                        this.columnsViewed.add(metricAV);
                    }
                    if (campo.hasAccumulatedValue()) {
                        List<ColorAlertMetadata> alertsAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAccValorAVMetaData.ACCUMULATED_VALUE_AV);
                        MetricCalculatedAccValorAVMetaData metricAccumValueAV = new MetricCalculatedAccValorAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertsAV);
                        this.addHierarchyLineMetricCalculated(metricAccumValueAV);
                        this.columnsViewed.add(metricAccumValueAV);
                    }
                    if (campo.hasAccumulatedParticipation()) {
                        MetricCalculatedParticipacaoMetaData metaDataAVAuxiliar;
                        if (!campo.hasVerticalAnalysis()) {
                            metaDataAVAuxiliar = new MetricCalculatedAVParticipationMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, null);
                            metaDataAVAuxiliar.setViewed(false);
                            this.addHierarchyLineMetricCalculated(metaDataAVAuxiliar);
                        } else {
                            metaDataAVAuxiliar = (MetricCalculatedParticipacaoMetaData) this.getMetricMetaDataRelativeField(campo.getTitle(), MetricCalculatedAVParticipationMetaData.AV);
                        }
                        List<ColorAlertMetadata> alertsAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAccParticipationAVMetaData.ACC_PARTICIPATION_AV);
                        MetricCalculatedAccParticipationAVMetaData metricPartAccumAV = new MetricCalculatedAccParticipationAVMetaData(metaDataAVAuxiliar, tipoAnaliseVerticalCampo, alertsAV);
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
                this.getDimensionsLastLevelLines().sort(new DimensaoMetricaComparator(this.metricsMap, new ArrayList<>(), metricOrderings));
            } else {
                List<Dimension> parentDimension = new ArrayList<>();
                this.buildDimensionReorderChildrenList(this, parentDimension);
                for (Dimension dimension : parentDimension) {
                    List<Dimension> dimensionLine = new ArrayList<>();
                    this.getLastLevelList(dimension.getDimensionsLine().values(), dimensionLine);
                    dimensionLine.sort(new DimensaoMetricaComparator(this.metricsMap, new ArrayList<>(), metricOrderings));
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
