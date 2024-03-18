package com.msoft.mbi.cube.multi.dimension;

import java.io.Serial;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DimensionColuna extends Dimension {

    @Serial
    private static final long serialVersionUID = 3691610278266628390L;
    private Dimension dimensionCuboPai;
    private Dimension dimensionLinha = new DimensionLinhaNula(cube);

    public DimensionColuna(Dimension dimensionPai, Dimension dimensionCuboPai, DimensaoMetaData metaData) {
        this(dimensionPai, metaData);
        this.dimensionCuboPai = dimensionCuboPai;
    }

    public DimensionColuna(Dimension dimensionLinha, Dimension dimensionPai, Dimension dimensionCuboPai, DimensaoMetaData metaData) {
        this(dimensionPai, dimensionCuboPai, metaData);
        this.dimensionLinha = dimensionLinha;
    }

    public DimensionColuna(Dimension dimensionPai, DimensaoMetaData metaData) {
        super(dimensionPai, metaData);
        this.dimensionsLine = new Dimensions();
        this.dimensionsColumn = new Dimensions();
    }

    @Override
    public Dimensions getDimensoesAbaixo() {
        return this.dimensionsColumn;
    }

    @Override
    public void setKeyValue() {
        this.keyValue = this.buildKeyValue().toString();
    }

    public void processar(ResultSet set) throws SQLException {
        Comparable valor = (Comparable) this.type.getValor(set, this.metaData.getColuna());
        valor = (Comparable) this.type.format(valor);
        this.setVisualizationValue(valor);
        DimensaoMetaData dimensaoOrdenacao = this.metaData.getDimensaoOrdenacao();
        if (dimensaoOrdenacao != null) {
            this.setValue((Comparable) dimensaoOrdenacao.getTipo().getValor(set, dimensaoOrdenacao.getColuna()));
        } else {
            this.setValue(valor);
        }

        Dimension dimensionColuna = this.parent.getDimensionsColumn().get(this);
        if (dimensionColuna == null) {
            int indiceDimensao = this.cube.getPoolDimensoes().indexOf(this);
            if (indiceDimensao != -1) {
                dimensionColuna = this.cube.getPoolDimensoes().get(indiceDimensao);
            } else {
                dimensionColuna = this;
                this.cube.addDimensaoPool(dimensionColuna);
            }
            this.parent.addDimensionsColumn(dimensionColuna);
            dimensionColuna.parent = this.dimensionCuboPai;
        }
        this.cube.getMapaMetricas().acumulaMetrica(this.dimensionLinha, this, set);

        if (this.dimensionLinha.getMetaData().isUltima()) {
            this.cube.getMapaMetricas().acumulaMetricaColuna(this, set);
        }

        Dimension dimensionCubo = this.dimensionCuboPai.getDimensionsColumn().get(this);
        if (dimensionCubo == null) {
            int indiceDimensao = this.cube.getPoolDimensoes().indexOf(this);
            if (indiceDimensao != -1) {
                dimensionCubo = this.cube.getPoolDimensoes().get(indiceDimensao);
            } else {
                dimensionCubo = new DimensionColuna(this.dimensionCuboPai, this.metaData);
                dimensionCubo.setValue(this.getValue());
                this.cube.addDimensaoPool(dimensionCubo);
            }
            this.dimensionCuboPai.addDimensionsColumn(dimensionCubo);
        }

        if (!this.metaData.isUltima()) {
            DimensionColuna dimensao = new DimensionColuna(this.dimensionLinha, dimensionColuna, dimensionCubo, this.metaData.getFilho());
            dimensao.processar(set);
        }
    }

    public void processarOutros(Dimension dimensionLinhaRemover, Dimension dimensionLinhaOutros, DimensionColuna dimensaoColunaOld) {
        this.setValue(dimensaoColunaOld.getValue());
        Dimension dimensionColuna = this.parent.getDimensionsColumn().get(this);
        if (dimensionColuna == null) {
            this.parent.addDimensionsColumn(this);
            dimensionColuna = this;
        }
        this.cube.getMapaMetricas().acumulaMetricaOutros(dimensionLinhaRemover, dimensionLinhaOutros, dimensaoColunaOld);
        Dimension dimensionCubo = this.dimensionCuboPai.getDimensionsColumn().get(this);
        if (dimensionCubo == null) {
            dimensionCubo = new DimensionColuna(this.dimensionCuboPai, this.metaData);
            dimensionCubo.setValue(this.getValue());
            this.dimensionCuboPai.addDimensionsColumn(dimensionCubo);
        }

    }

}
