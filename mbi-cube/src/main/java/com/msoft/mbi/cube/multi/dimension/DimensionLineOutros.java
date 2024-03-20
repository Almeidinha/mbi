package com.msoft.mbi.cube.multi.dimension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class DimensionLineOutros extends DimensionLine {

    public final static String VALOR_OUTROS = "OUTROS";

    public DimensionLineOutros(Dimension pai, DimensaoMetaData metaData) {
        super(pai, metaData);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void processar(Dimension dimensionLinhaRemover) {
        this.setValue((Comparable) VALOR_OUTROS);
        this.setVisualizationValue((Comparable) VALOR_OUTROS);
        Dimension dimensionLinhaOutros = this.parent.getDimensionsLine().get(this);
        if (dimensionLinhaOutros == null) {
            this.parent.addDimensionLine(this);
            dimensionLinhaOutros = this;
        }

        this.cube.getMetricsMap().accumulateMetricLineOthers(dimensionLinhaRemover, dimensionLinhaOutros);
        Iterator<Dimension> iDimensoesColuna = dimensionLinhaRemover.getDimensionsColumn().values().iterator();
        this.processarColunasOutros(dimensionLinhaRemover, dimensionLinhaOutros, iDimensoesColuna, dimensionLinhaOutros, this.metaData.getCubo());
    }

    private void processarColunasOutros(Dimension dimensionLinhaRemover, Dimension dimensionLinhaOutros, Iterator<Dimension> iDimensoesColuna,
                                        Dimension dimensionPai, Dimension dimensionCuboPai) {
        while (iDimensoesColuna.hasNext()) {
            DimensionColumn dimensaoColunaOld = (DimensionColumn) iDimensoesColuna.next();
            DimensionColumn dimensaoColunaOutros = new DimensionColumn(this, dimensionPai, dimensionCuboPai, dimensaoColunaOld.getMetaData());
            dimensaoColunaOutros.processOthers(dimensionLinhaRemover, dimensionLinhaOutros, dimensaoColunaOld);
            if (!dimensaoColunaOld.getDimensionsColumn().isEmpty()) {
                this.processarColunasOutros(dimensionLinhaRemover, dimensionLinhaOutros, dimensaoColunaOld.getDimensionsColumn().values().iterator(),
                        dimensaoColunaOld, dimensionCuboPai.getDimensionsColumn().get(dimensaoColunaOutros));
            }
        }
    }

    @Override
    public void process(ResultSet set) throws SQLException {

    }

    @Override
    public int getColspanImpressaoLinha() {
        DimensaoMetaData dimensaoRankeada = ((DimensaoOutrosMetaData) this.getMetaData()).getMetaDataDimensaoRanking();
        int niveisAbaixo = dimensaoRankeada.getQtdNiveisAbaixoComSequencia();
        int colspan = niveisAbaixo + 1;
        return colspan;
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

    public String buscaPropriedadeAlertasMetricasLinha(List<MetricMetaData> metricasMetaData, List<String> funcoes) {
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Comparable<Object> getOrderValue() {
        return (Comparable) (" " + VALOR_OUTROS);
    }

    @Override
    public Integer getRankingSequence() {
        return null;
    }
}
