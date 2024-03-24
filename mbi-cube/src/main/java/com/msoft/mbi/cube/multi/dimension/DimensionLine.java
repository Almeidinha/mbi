package com.msoft.mbi.cube.multi.dimension;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DimensionLine extends Dimension {


    public DimensionLine(Dimension pai, DimensionMetaData metaData) {
        super(pai, metaData);
        this.dimensionsLine = new Dimensions();
        this.dimensionsColumn = new Dimensions();
    }

    public int getColspanLinePrint() {
        return 1;
    }

    @Override
    public Dimensions getDimensionsBelow() {
        return this.dimensionsLine;
    }

    @Override
    public void setKeyValue() {
        this.keyValue = this.buildCompleteKeyValue().toString();
    }

    public void process(ResultSet resultSet) throws SQLException {
        processValue(resultSet);
        processDimensionOrdering(resultSet);
        processDimensionLine();
        this.cube.getMetricsMap().accumulateMetricLine(this, resultSet);
        processDimensionChildren(resultSet);
        processColumnHierarchy(resultSet);
    }

    private void processValue(ResultSet resultSet) throws SQLException {
        Object rawValue = type.getValue(resultSet, metaData.getColumn());
        Object formattedValue = type.format(rawValue);
        setVisualizationValue(castToComparable(formattedValue));

        if (metaData.getOrderingDimension() != null) {
            DimensionMetaData dimensionOrdering = metaData.getOrderingDimension();
            Object orderingValue = dimensionOrdering.getDataType().getValue(resultSet, dimensionOrdering.getColumn());
            setValue(castToComparable(orderingValue));
        } else {
            setValue(castToComparable(formattedValue));
        }
    }

    private void processDimensionOrdering(ResultSet resultSet) throws SQLException {
        DimensionMetaData dimensionOrdering = metaData.getOrderingDimension();
        if (dimensionOrdering == null) return;
        Comparable<String> orderingValue = castToComparable(dimensionOrdering.getDataType().getValue(resultSet, dimensionOrdering.getColumn()));
        setValue(orderingValue);
    }

    private void processDimensionLine() {
        Dimension dimensionLine = parent.getDimensionsLine().computeIfAbsent(this, k -> {
            int dimensionIndex = cube.getDimensionsPool().indexOf(this);
            return (dimensionIndex != -1) ? cube.getDimensionsPool().get(dimensionIndex) : this;
        });
        parent.addDimensionLine(dimensionLine);
    }

    private void processDimensionChildren(ResultSet resultSet) throws SQLException {
        if (!metaData.isLast()) {
            Dimension dimension = new DimensionLine(parent.getDimensionsLine().get(this), metaData.getChild());
            dimension.process(resultSet);
        }
    }

    private void processColumnHierarchy(ResultSet resultSet) throws SQLException {
        if (!metaData.getCube().getHierarchyColumn().isEmpty()) {
            Dimension dimensionColumn = new DimensionColumn(this, parent.getDimensionsLine().get(this), metaData.getCube(),
                    metaData.getCube().getHierarchyColumn().get(0));
            dimensionColumn.process(resultSet);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> T castToComparable(Object obj) {
        return (T) obj;
    }
}
