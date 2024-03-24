package com.msoft.mbi.cube.multi.dimension;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DimensionColumn extends Dimension {

    private Dimension dimensionParentCube;
    private Dimension dimensionLine = new DimensionLineNull(cube);

    public DimensionColumn(Dimension dimensionPai, Dimension dimensionCubeParent, DimensionMetaData metaData) {
        this(dimensionPai, metaData);
        this.dimensionParentCube = dimensionCubeParent;
    }

    public DimensionColumn(Dimension dimensionLine, Dimension dimensionPai, Dimension dimensionParentCube, DimensionMetaData metaData) {
        this(dimensionPai, dimensionParentCube, metaData);
        this.dimensionLine = dimensionLine;
    }

    public DimensionColumn(Dimension dimensionPai, DimensionMetaData metaData) {
        super(dimensionPai, metaData);
        this.dimensionsLine = new Dimensions();
        this.dimensionsColumn = new Dimensions();
    }

    @Override
    public Dimensions getDimensionsBelow() {
        return this.dimensionsColumn;
    }

    @Override
    public void setKeyValue() {
        this.keyValue = this.buildKeyValue().toString();
    }

    @SuppressWarnings("unchecked")
    public void process(ResultSet set) throws SQLException {
        Comparable<String> value = (Comparable<String>) this.type.getValue(set, this.metaData.getColumn());
        value = (Comparable<String>) this.type.format(value);
        this.setVisualizationValue(value);
        DimensionMetaData dimensionOrdering = this.metaData.getOrderingDimension();
        if (dimensionOrdering != null) {
            this.setValue((Comparable<String>) dimensionOrdering.getDataType().getValue(set, dimensionOrdering.getColumn()));
        } else {
            this.setValue(value);
        }

        Dimension dimensionColumn = this.parent.getDimensionsColumn().get(this);
        if (dimensionColumn == null) {
            int dimensionIndex = this.cube.getDimensionsPool().indexOf(this);
            if (dimensionIndex != -1) {
                dimensionColumn = this.cube.getDimensionsPool().get(dimensionIndex);
            } else {
                dimensionColumn = this;
                this.cube.addDimensionPool(dimensionColumn);
            }
            this.parent.addDimensionsColumn(dimensionColumn);
            dimensionColumn.parent = this.dimensionParentCube;
        }
        this.cube.getMetricsMap().accumulateMetric(this.dimensionLine, this, set);

        if (this.dimensionLine.getMetaData().isLast()) {
            this.cube.getMetricsMap().accumulateMetricColumn(this, set);
        }

        Dimension dimensionCube = this.dimensionParentCube.getDimensionsColumn().get(this);
        if (dimensionCube == null) {
            int dimensionIndex = this.cube.getDimensionsPool().indexOf(this);
            if (dimensionIndex != -1) {
                dimensionCube = this.cube.getDimensionsPool().get(dimensionIndex);
            } else {
                dimensionCube = new DimensionColumn(this.dimensionParentCube, this.metaData);
                dimensionCube.setValue(this.getValue());
                this.cube.addDimensionPool(dimensionCube);
            }
            this.dimensionParentCube.addDimensionsColumn(dimensionCube);
        }

        if (!this.metaData.isLast()) {
            DimensionColumn dimension = new DimensionColumn(this.dimensionLine, dimensionColumn, dimensionCube, this.metaData.getChild());
            dimension.process(set);
        }
    }

    public void processOthers(Dimension dimensionLineRemove, Dimension dimensionLineOthers, DimensionColumn dimensionLineOld) {
        this.setValue(dimensionLineOld.getValue());
        Dimension dimensionColumn = this.parent.getDimensionsColumn().get(this);
        if (dimensionColumn == null) {
            this.parent.addDimensionsColumn(this);
        }
        this.cube.getMetricsMap().accumulateMetricOthers(dimensionLineRemove, dimensionLineOthers, dimensionLineOld);
        Dimension dimensionCubo = this.dimensionParentCube.getDimensionsColumn().get(this);
        if (dimensionCubo == null) {
            dimensionCubo = new DimensionColumn(this.dimensionParentCube, this.metaData);
            dimensionCubo.setValue(this.getValue());
            this.dimensionParentCube.addDimensionsColumn(dimensionCubo);
        }

    }

}
