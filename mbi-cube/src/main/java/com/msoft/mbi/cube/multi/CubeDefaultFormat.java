package com.msoft.mbi.cube.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoGeral;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensaoAuxiliarMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoMetricaComparator;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFilters;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFiltersAccumulatedValue;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metaData.CubeMetaData;
import com.msoft.mbi.cube.multi.metaData.OrdenacaoCampoComparator;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizarLinhaMetrica;
import com.msoft.mbi.cube.multi.metrics.MetricOrdering;
import com.msoft.mbi.cube.multi.metrics.additive.MetricAdditiveMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.CalculoHierarquiaOrdenacao;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAcumuladoParticipacaoAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAcumuladoValorAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoMetaData;

public class CubeDefaultFormat extends Cube {

    private DimensaoMetaData lastOrderedDimension;

    protected CubeDefaultFormat(CubeMetaData metaData) {
        super(metaData);
    }

    @Override
    protected void factory() {
        CubeMetaData cubeMetaData = (CubeMetaData) this.metaData;
        cubeMetaData.ordenaCampos();
        cubeMetaData.ordenaCamposDimensao();

        Map<CampoMetaData, DimensaoMetaData> dimensions = new HashMap<>();

        List<CampoMetaData> dimensionsByOrder = new ArrayList<>(cubeMetaData.getCamposDimensao());
        OrdenacaoCampoComparator comparator = new OrdenacaoCampoComparator();
        dimensionsByOrder.sort(comparator);
        DimensaoAuxiliarMetaData dimensaoMetaDataFicticia = new DimensaoAuxiliarMetaData();
        this.addHierarchyLine(dimensaoMetaDataFicticia);
        for (CampoMetaData campo : dimensionsByOrder) {
            if ("S".equals(campo.getPadrao())) {
                DimensaoMetaData dimensaoMetaData = DimensaoMetaData.factory(campo);
                this.addHierarchyLine(dimensaoMetaData);
                dimensions.put(campo, dimensaoMetaData);
                if (campo.getOrdem() > 0) {
                    this.lastOrderedDimension = dimensaoMetaData;
                }
            }
        }

        for (CampoMetaData campo : cubeMetaData.getCampos()) {
            if (CampoMetaData.DIMENSAO.equals(campo.getTipoCampo())) {
                if ("S".equals(campo.getPadrao())) {
                    this.columnsViewed.add(dimensions.get(campo));
                }
            } else {
                AnaliseParticipacaoTipo tipoAnaliseVerticalCampo = AnaliseParticipacaoTipoGeral.getInstance();
                String visualizacaoMetrica = this.getMetricVisualizationStatus(campo);
                if (!CampoMetaData.METRICA_NAO_ADICIONADA.equals(visualizacaoMetrica)) {
                    MetricMetaData metaDataCampoOriginal;
                    if (!campo.isExpressao()) {
                        metaDataCampoOriginal = MetricAdditiveMetaData.factory(campo);
                        this.addHierarchyLineMetricAdditive((MetricAdditiveMetaData) metaDataCampoOriginal);
                    } else {
                        campo.setNomeCampo(this.convertConditionalExpression(campo.getNomeCampo()));

                        metaDataCampoOriginal = MetricCalculatedMetaData.factory(campo);

                        Double valorExpr = null;
                        Double resultExpr = null;

                        try {
                            resultExpr = ((MetricCalculatedMetaData) metaDataCampoOriginal).createCalculo().calculaValor();
                            valorExpr = Double.valueOf(campo.getNomeCampo());
                        } catch (Exception e) {
                        }

                        if (valorExpr != null && resultExpr != null && resultExpr.equals(valorExpr)) {
                            metaDataCampoOriginal = MetricAdditiveMetaData.factory(campo);
                            this.addHierarchyLineMetricAdditive((MetricAdditiveMetaData) metaDataCampoOriginal);
                        } else {
                            this.addHierarchyLineMetricCalculated((MetricCalculatedMetaData) metaDataCampoOriginal);
                        }
                    }

                    if (CampoMetaData.METRICA_VISUALIZACAO_RESTRITA.equals(visualizacaoMetrica)) {
                        metaDataCampoOriginal.setViewed(false);
                    } else {
                        this.columnsViewed.add(metaDataCampoOriginal);
                    }

                    if (campo.temAnaliseVertical()) {
                        List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricCalculatedParticipacaoAVMetaData.AV);
                        MetricCalculatedParticipacaoAVMetaData metricaAV = new MetricCalculatedParticipacaoAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertasAV);
                        metricaAV.setTotalLines(true);
                        this.addHierarchyLineMetricCalculated(metricaAV);
                        this.columnsViewed.add(metricaAV);
                    }
                    if (campo.temValorAcumulado()) {
                        List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricCalculatedAcumuladoValorAVMetaData.VALOR_ACUMULADO_AV);
                        MetricCalculatedAcumuladoValorAVMetaData metricaValorAcumuladoAV = new MetricCalculatedAcumuladoValorAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertasAV);
                        this.addHierarchyLineMetricCalculated(metricaValorAcumuladoAV);
                        this.columnsViewed.add(metricaValorAcumuladoAV);
                    }
                    if (campo.temParticipacaoAcumulada()) {
                        MetricCalculatedParticipacaoMetaData metaDataAVAuxiliar = null;
                        if (!campo.temAnaliseVertical()) {
                            metaDataAVAuxiliar = new MetricCalculatedParticipacaoAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, null);
                            metaDataAVAuxiliar.setViewed(false);
                            this.addHierarchyLineMetricCalculated(metaDataAVAuxiliar);
                        } else {
                            metaDataAVAuxiliar = (MetricCalculatedParticipacaoMetaData) this.getMetricMetaDataRelativeField(campo.getTituloCampo(), MetricCalculatedParticipacaoAVMetaData.AV);
                        }
                        List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricCalculatedAcumuladoParticipacaoAVMetaData.PARTICIPACAO_ACUMULADA_AV);
                        MetricCalculatedAcumuladoParticipacaoAVMetaData metricaPartAcumAV = new MetricCalculatedAcumuladoParticipacaoAVMetaData(metaDataAVAuxiliar, tipoAnaliseVerticalCampo, alertasAV);
                        this.addHierarchyLineMetricCalculated(metricaPartAcumAV);
                        this.columnsViewed.add(metricaPartAcumAV);
                    }
                }
            }
        }

        List<MetricCalculatedMetaData> metricasCalculadas = this.getHierarchyMetricCalculated();
        CalculoHierarquiaOrdenacao hierarquiaCalculo = new CalculoHierarquiaOrdenacao(metricasCalculadas);
        metricasCalculadas = hierarquiaCalculo.getMetricasCalculadasOrdenadas();
        this.setHierarchyMetricCalculated(metricasCalculadas);

        if (cubeMetaData.getExpressaoFiltrosMetrica() != null && !"".equals(cubeMetaData.getExpressaoFiltrosMetrica().trim())) {
            String expressaoFiltro = this.convertMetricFilterToFieldTitle(cubeMetaData.getExpressaoFiltrosMetrica());
            this.metricFilters = new MetricFilters(expressaoFiltro, MetricaValorUtilizarLinhaMetrica.getInstance());
        }
        if (cubeMetaData.getExpressaoFiltrosAcumulado() != null && !"".equals(cubeMetaData.getExpressaoFiltrosAcumulado().trim())) {
            String expressaoFiltro = this.convertMetricFilterToFieldTitle(cubeMetaData.getExpressaoFiltrosAcumulado());
            this.metricFiltersAccumulatedValue = new MetricFiltersAccumulatedValue(expressaoFiltro);
        }
    }

    @Override
    public void UpdateSequenceRanking(Iterator<Dimension> dimensionIterator) {
        List<Dimension> lastLevelDimension = this.getDimensionsLastLevelLines();
        int sequencia = 1;
        for (Dimension lastLine : lastLevelDimension) {
            lastLine.setRankingSequence(sequencia++);
        }
    }

    private void applyRanking(List<Dimension> dimensoes) {
        Iterator<Dimension> iDimensoes = dimensoes.iterator();
        ColumnMetaData firstColumn = this.getColumnsViewed().get(0);
        if (firstColumn.hasSequenceFields() && firstColumn.getFunctionRanking() != null) {
            this.applyRanking(iDimensoes, firstColumn.getFunctionRanking(), dimensoes.size());
        }
    }

    @Override
    protected void verifyRanking() {
        this.applyRanking(this.getDimensionsLastLevelLines());
    }

    @Override
    protected void removeDimensionsFiltersFunction(Dimension dimensionAdicionarOutros, List<Dimension> dimensions) {
        List<Dimension> lastLevelDimension = this.getDimensionsLastLevelLines();
        for (Dimension dimensionLinhaRemover : dimensions) {
            Dimension dimensionPai = dimensionLinhaRemover.getParent();
            dimensionPai.removeDimensionLine(dimensionLinhaRemover);
            this.metricsMap.removeMetricLine(dimensionLinhaRemover);
            lastLevelDimension.remove(dimensionLinhaRemover);
        }
    }

    @Override
    protected void reorderData(List<MetricOrdering> metricOrderings) {
        if (!metricOrderings.isEmpty()) {
            if (this.lastOrderedDimension == null) {
                this.getDimensionsLastLevelLines().sort(new DimensaoMetricaComparator(this.metricsMap, new ArrayList<>(), metricOrderings));
            } else {
                List<Dimension> lDimensoesPaiReordenar = new ArrayList<>();
                this.buildDimensionReorderChildrenList(this, lDimensoesPaiReordenar);
                for (Dimension dimensionPaiReordenar : lDimensoesPaiReordenar) {
                    List<Dimension> dimensoesLinhaAux = new ArrayList<>();
                    this.getLastLevelList(dimensionPaiReordenar.getDimensionsLine().values(), dimensoesLinhaAux);
                    dimensoesLinhaAux.sort(new DimensaoMetricaComparator(this.metricsMap, new ArrayList<>(), metricOrderings));
                }
            }
        }
    }

    private void buildDimensionReorderChildrenList(Dimension dimensionPai, List<Dimension> dimensoesPai) {
        Iterator<Dimension> iDimensoes = dimensionPai.getDimensionsLine().values().iterator();
        Dimension dimension;
        while (iDimensoes.hasNext()) {
            dimension = iDimensoes.next();
            if (dimension.getMetaData().equals(this.lastOrderedDimension)) {
                dimensoesPai.add(dimension);
            } else {
                buildDimensionReorderChildrenList(dimension, dimensoesPai);
                break;
            }
        }
    }

    @Override
    protected void addMetricOrdering(List<MetricOrdering> metricOrderings, List<MetricOrdering> orderingList) {
        orderingList.addAll(metricOrderings);
    }

}
