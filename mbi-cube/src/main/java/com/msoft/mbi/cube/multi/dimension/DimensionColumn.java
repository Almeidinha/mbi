package com.msoft.mbi.cube.multi.dimension;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DimensionColumn extends Dimension {

    private Dimension dimensionCuboPai;
    private Dimension dimensionLinha = new DimensionLinhaNull(cube);

    public DimensionColumn(Dimension dimensionPai, Dimension dimensionCubeParent, DimensaoMetaData metaData) {
        this(dimensionPai, metaData);
        this.dimensionCuboPai = dimensionCubeParent;
    }

    public DimensionColumn(Dimension dimensionLine, Dimension dimensionPai, Dimension dimensionCuboPai, DimensaoMetaData metaData) {
        this(dimensionPai, dimensionCuboPai, metaData);
        this.dimensionLinha = dimensionLine;
    }

    public DimensionColumn(Dimension dimensionPai, DimensaoMetaData metaData) {
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
        Comparable<String> valor = (Comparable<String>) this.type.getValue(set, this.metaData.getColuna());
        valor = (Comparable<String>) this.type.format(valor);
        this.setVisualizationValue(valor);
        DimensaoMetaData dimensionOrdering = this.metaData.getDimensaoOrdenacao();
        if (dimensionOrdering != null) {
            this.setValue((Comparable<String>) dimensionOrdering.getTipo().getValue(set, dimensionOrdering.getColuna()));
        } else {
            this.setValue(valor);
        }

        Dimension dimensionColuna = this.parent.getDimensionsColumn().get(this);
        if (dimensionColuna == null) {
            int indiceDimensao = this.cube.getDimensionsPool().indexOf(this);
            if (indiceDimensao != -1) {
                dimensionColuna = this.cube.getDimensionsPool().get(indiceDimensao);
            } else {
                dimensionColuna = this;
                this.cube.addDimensionPool(dimensionColuna);
            }
            this.parent.addDimensionsColumn(dimensionColuna);
            dimensionColuna.parent = this.dimensionCuboPai;
        }
        this.cube.getMetricsMap().accumulateMetric(this.dimensionLinha, this, set);

        if (this.dimensionLinha.getMetaData().isUltima()) {
            this.cube.getMetricsMap().accumulateMetricColumn(this, set);
        }

        Dimension dimensionCubo = this.dimensionCuboPai.getDimensionsColumn().get(this);
        if (dimensionCubo == null) {
            int indiceDimensao = this.cube.getDimensionsPool().indexOf(this);
            if (indiceDimensao != -1) {
                dimensionCubo = this.cube.getDimensionsPool().get(indiceDimensao);
            } else {
                dimensionCubo = new DimensionColumn(this.dimensionCuboPai, this.metaData);
                dimensionCubo.setValue(this.getValue());
                this.cube.addDimensionPool(dimensionCubo);
            }
            this.dimensionCuboPai.addDimensionsColumn(dimensionCubo);
        }

        if (!this.metaData.isUltima()) {
            DimensionColumn dimension = new DimensionColumn(this.dimensionLinha, dimensionColuna, dimensionCubo, this.metaData.getFilho());
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
        Dimension dimensionCubo = this.dimensionCuboPai.getDimensionsColumn().get(this);
        if (dimensionCubo == null) {
            dimensionCubo = new DimensionColumn(this.dimensionCuboPai, this.metaData);
            dimensionCubo.setValue(this.getValue());
            this.dimensionCuboPai.addDimensionsColumn(dimensionCubo);
        }

    }

}
