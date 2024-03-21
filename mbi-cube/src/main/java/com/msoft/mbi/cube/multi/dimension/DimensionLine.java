package com.msoft.mbi.cube.multi.dimension;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DimensionLine extends Dimension {


    public DimensionLine(Dimension pai, DimensionMetaData metaData) {
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
        Comparable<String> valor = (Comparable<String>) type.getValue(resultSet, metaData.getColumn());
        valor = (Comparable<String>) type.format(valor);
        setVisualizationValue(valor);
        DimensionMetaData dimensaoOrdenacao = metaData.getOrderingDimension();
        setValue(dimensaoOrdenacao != null ? (Comparable<String>) dimensaoOrdenacao.getDataType().getValue(resultSet, dimensaoOrdenacao.getColumn()) : valor);
    }

    @SuppressWarnings("unchecked")
    private void processDimensionOrdering(ResultSet resultSet) throws SQLException {
        DimensionMetaData dimensaoOrdenacao = metaData.getOrderingDimension();
        if (dimensaoOrdenacao == null) return;
        Comparable<String> valorOrdenacao = (Comparable<String>) dimensaoOrdenacao.getDataType().getValue(resultSet, dimensaoOrdenacao.getColumn());
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
}
