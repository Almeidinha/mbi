package com.msoft.mbi.cube.multi.dimension;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DimensionLinha extends Dimension {


    public DimensionLinha(Dimension pai, DimensaoMetaData metaData) {
        super(pai, metaData);
        this.dimensionsLine = new Dimensions();
        this.dimensionsColumn = new Dimensions();
    }

    public int getColspanImpressaoLinha() {
        return 1;// default
    }

    @Override
    public Dimensions getDimensoesAbaixo() {
        return this.dimensionsLine;
    }

    @Override
    public void setKeyValue() {
        this.keyValue = this.buildCompleteKeyValue().toString();
    }

    public void processar(ResultSet resultSet) throws SQLException {
        processValor(resultSet);
        processDimensaoOrdenacao(resultSet);
        processDimensaoLinha();
        this.cube.getMapaMetricas().accumulateMetricLine(this, resultSet);
        processFilhoDimensao(resultSet);
        processHierarquiaColuna(resultSet);
    }

    @SuppressWarnings("unchecked")
    private void processValor(ResultSet resultSet) throws SQLException {
        Comparable<String> valor = (Comparable<String>) type.getValor(resultSet, metaData.getColuna());
        valor = (Comparable<String>) type.format(valor);
        setVisualizationValue(valor);
        DimensaoMetaData dimensaoOrdenacao = metaData.getDimensaoOrdenacao();
        setValue(dimensaoOrdenacao != null ? (Comparable<String>) dimensaoOrdenacao.getTipo().getValor(resultSet, dimensaoOrdenacao.getColuna()) : valor);
    }

    @SuppressWarnings("unchecked")
    private void processDimensaoOrdenacao(ResultSet resultSet) throws SQLException {
        DimensaoMetaData dimensaoOrdenacao = metaData.getDimensaoOrdenacao();
        if (dimensaoOrdenacao == null) return;
        Comparable<String> valorOrdenacao = (Comparable<String>) dimensaoOrdenacao.getTipo().getValor(resultSet, dimensaoOrdenacao.getColuna());
        setValue(valorOrdenacao);
    }

    private void processDimensaoLinha() {
        Dimension dimensionLinha = parent.getDimensionsLine().computeIfAbsent(this, k -> {
            int indiceDimensao = cube.getPoolDimensoes().indexOf(this);
            return (indiceDimensao != -1) ? cube.getPoolDimensoes().get(indiceDimensao) : this;
        });
        parent.addDimensionLine(dimensionLinha);
    }

    private void processFilhoDimensao(ResultSet resultSet) throws SQLException {
        if (!metaData.isUltima()) {
            Dimension dimension = new DimensionLinha(parent.getDimensionsLine().get(this), metaData.getFilho());
            dimension.processar(resultSet);
        }
    }

    private void processHierarquiaColuna(ResultSet resultSet) throws SQLException {
        if (!metaData.getCubo().getHierarquiaColuna().isEmpty()) {
            Dimension dimensionColuna = new DimensionColuna(this, parent.getDimensionsLine().get(this), metaData.getCubo(),
                    metaData.getCubo().getHierarquiaColuna().get(0));
            dimensionColuna.processar(resultSet);
        }
    }
}
