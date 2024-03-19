package com.msoft.mbi.cube.multi.dimension;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DimensionLine extends Dimension {


    public DimensionLine(Dimension pai, DimensaoMetaData metaData) {
        super(pai, metaData);
        this.dimensionsLine = new Dimensions();
        this.dimensionsColumn = new Dimensions();
    }

    public int getColspanImpressaoLinha() {
        return 1;// default
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

    @SuppressWarnings("unchecked")
    private void processValue(ResultSet resultSet) throws SQLException {
        Comparable<String> valor = (Comparable<String>) type.getValue(resultSet, metaData.getColuna());
        valor = (Comparable<String>) type.format(valor);
        setVisualizationValue(valor);
        DimensaoMetaData dimensaoOrdenacao = metaData.getDimensaoOrdenacao();
        setValue(dimensaoOrdenacao != null ? (Comparable<String>) dimensaoOrdenacao.getTipo().getValue(resultSet, dimensaoOrdenacao.getColuna()) : valor);
    }

    @SuppressWarnings("unchecked")
    private void processDimensionOrdering(ResultSet resultSet) throws SQLException {
        DimensaoMetaData dimensaoOrdenacao = metaData.getDimensaoOrdenacao();
        if (dimensaoOrdenacao == null) return;
        Comparable<String> valorOrdenacao = (Comparable<String>) dimensaoOrdenacao.getTipo().getValue(resultSet, dimensaoOrdenacao.getColuna());
        setValue(valorOrdenacao);
    }

    private void processDimensionLine() {
        Dimension dimensionLinha = parent.getDimensionsLine().computeIfAbsent(this, k -> {
            int indiceDimensao = cube.getDimensionsPool().indexOf(this);
            return (indiceDimensao != -1) ? cube.getDimensionsPool().get(indiceDimensao) : this;
        });
        parent.addDimensionLine(dimensionLinha);
    }

    private void processDimensionChildren(ResultSet resultSet) throws SQLException {
        if (!metaData.isUltima()) {
            Dimension dimension = new DimensionLine(parent.getDimensionsLine().get(this), metaData.getFilho());
            dimension.process(resultSet);
        }
    }

    private void processColumnHierarchy(ResultSet resultSet) throws SQLException {
        if (!metaData.getCubo().getHierarchyColumn().isEmpty()) {
            Dimension dimensionColumn = new DimensionColumn(this, parent.getDimensionsLine().get(this), metaData.getCubo(),
                    metaData.getCubo().getHierarchyColumn().get(0));
            dimensionColumn.process(resultSet);
        }
    }
}
