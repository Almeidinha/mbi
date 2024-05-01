package com.msoft.mbi.cube.multi.dimension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.msoft.mbi.cube.multi.coloralertcondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class DimensionLineOutros extends DimensionLine {

    public final static String VALUE_OTHERS = "OUTROS";

    public DimensionLineOutros(Dimension pai, DimensionMetaData metaData) {
        super(pai, metaData);
    }

    public void process(Dimension dimensionLine) {
        this.setValue(VALUE_OTHERS);
        this.setVisualizationValue(VALUE_OTHERS);
        Dimension dimensionLinhaOutros = this.parent.getDimensionsLine().get(this);
        if (dimensionLinhaOutros == null) {
            this.parent.addDimensionLine(this);
            dimensionLinhaOutros = this;
        }

        this.cube.getMetricsMap().accumulateMetricLineOthers(dimensionLine, dimensionLinhaOutros);

        this.processColumnOthers(dimensionLine, dimensionLinhaOutros, dimensionLine.getDimensionsColumn().values(), dimensionLinhaOutros, this.metaData.getCube());
    }

    private void processColumnOthers(Dimension dimensionLines, Dimension dimensionLineOthers, Collection<Dimension> dimensionColumns,
                                     Dimension dimensionParent, Dimension dimensionCubeParent) {
        for (Dimension oldDimensionColumn : dimensionColumns) {
            DimensionColumn dimensionColumnOthers = new DimensionColumn(this, dimensionParent, dimensionCubeParent, oldDimensionColumn.getMetaData());
            dimensionColumnOthers.processOthers(dimensionLines, dimensionLineOthers, (DimensionColumn) oldDimensionColumn);
            if (!oldDimensionColumn.getDimensionsColumn().isEmpty()) {
                this.processColumnOthers(dimensionLines, dimensionLineOthers, oldDimensionColumn.getDimensionsColumn().values(),
                        oldDimensionColumn, dimensionCubeParent.getDimensionsColumn().get(dimensionColumnOthers));
            }
        }
    }

    @Override
    public void process(ResultSet set) throws SQLException {

    }

    @Override
    public int getColspanLinePrint() {
        DimensionMetaData rankedDimension = ((DimensionMetaDataOthers) this.getMetaData()).getDimensionMetaDataRanking();
        int lowerLevels = rankedDimension.getLowerLevelSequenceCount();
        return lowerLevels + 1;
    }

    @Override
    public int countPartialAggregatesInHierarchy() {
        return 0;
    }

    @Override
    public int getTotalSize() {
        return 1;
    }

    @Override
    public String searchDimensionAlertLineProperty() {
        return null;
    }

    @Override
    public String searchDimensionAlertCellProperty() {
        return null;
    }

    public String searchMetricLineAlertProps(List<MetricMetaData> metricsMetaData, List<String> functions) {
        return null;
    }

    @Override
    public String searchAlertMetricCell(List<ColorAlertConditionsMetrica> alertConditions, Double value, Dimension dimensionColumn) {
        return null;
    }

    @Override
    public String getMetricDefaultStyles(int currentLine) {
        return CellProperty.CELL_PROPERTY_OTHERS;
    }


    @Override
    public Comparable<Object> getOrderValue() {
        return o -> (" " + VALUE_OTHERS).compareTo(o.toString());
    }

    @Override
    public Integer getRankingSequence() {
        return null;
    }
}
