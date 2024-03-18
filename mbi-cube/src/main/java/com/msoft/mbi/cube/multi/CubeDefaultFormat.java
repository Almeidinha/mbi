package com.msoft.mbi.cube.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoGeral;
import com.msoft.mbi.cube.multi.column.ColunaMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensaoAuxiliarMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoMetricaComparator;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFilters;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFiltersAccumulatedValue;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metaData.CuboMetaData;
import com.msoft.mbi.cube.multi.metaData.OrdenacaoCampoComparator;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizarLinhaMetrica;
import com.msoft.mbi.cube.multi.metrics.OrdenacaoMetrica;
import com.msoft.mbi.cube.multi.metrics.additive.MetricaAditivaMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.CalculoHierarquiaOrdenacao;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaAcumuladoParticipacaoAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaAcumuladoValorAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaParticipacaoAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaParticipacaoMetaData;

public class CubeDefaultFormat extends Cubo {

    private DimensaoMetaData lastOrderedDimension;

    protected CubeDefaultFormat(CuboMetaData metaData) {
        super(metaData);
    }

    @Override
    protected void factory() {
        CuboMetaData cuboMetaData = (CuboMetaData) this.metaData;
        cuboMetaData.ordenaCampos();
        cuboMetaData.ordenaCamposDimensao();

        Map<CampoMetaData, DimensaoMetaData> dimensions = new HashMap<>();

        List<CampoMetaData> dimensionsByOrder = new ArrayList<>(cuboMetaData.getCamposDimensao());
        OrdenacaoCampoComparator comparator = new OrdenacaoCampoComparator();
        dimensionsByOrder.sort(comparator);
        DimensaoAuxiliarMetaData dimensaoMetaDataFicticia = new DimensaoAuxiliarMetaData();
        this.addHierarquiaLinha(dimensaoMetaDataFicticia);
        for (CampoMetaData campo : dimensionsByOrder) {
            if ("S".equals(campo.getPadrao())) {
                DimensaoMetaData dimensaoMetaData = DimensaoMetaData.factory(campo);
                this.addHierarquiaLinha(dimensaoMetaData);
                dimensions.put(campo, dimensaoMetaData);
                if (campo.getOrdem() > 0) {
                    this.lastOrderedDimension = dimensaoMetaData;
                }
            }
        }

        for (CampoMetaData campo : cuboMetaData.getCampos()) {
            if (CampoMetaData.DIMENSAO.equals(campo.getTipoCampo())) {
                if ("S".equals(campo.getPadrao())) {
                    this.colunasVisualizadas.add(dimensions.get(campo));
                }
            } else {
                AnaliseParticipacaoTipo tipoAnaliseVerticalCampo = AnaliseParticipacaoTipoGeral.getInstance();
                String visualizacaoMetrica = this.getStatusVisualizacaoMetrica(campo);
                if (!CampoMetaData.METRICA_NAO_ADICIONADA.equals(visualizacaoMetrica)) {
                    MetricaMetaData metaDataCampoOriginal;
                    if (!campo.isExpressao()) {
                        metaDataCampoOriginal = MetricaAditivaMetaData.factory(campo);
                        this.addHierarquiaMetricaAditiva((MetricaAditivaMetaData) metaDataCampoOriginal);
                    } else {
                        campo.setNomeCampo(this.converteExpressaoCondicional(campo.getNomeCampo()));

                        metaDataCampoOriginal = MetricaCalculadaMetaData.factory(campo);

                        Double valorExpr = null;
                        Double resultExpr = null;

                        try {
                            resultExpr = ((MetricaCalculadaMetaData) metaDataCampoOriginal).createCalculo().calculaValor();
                            valorExpr = Double.valueOf(campo.getNomeCampo());
                        } catch (Exception e) {
                        }

                        if (valorExpr != null && resultExpr != null && resultExpr.equals(valorExpr)) {
                            metaDataCampoOriginal = MetricaAditivaMetaData.factory(campo);
                            this.addHierarquiaMetricaAditiva((MetricaAditivaMetaData) metaDataCampoOriginal);
                        } else {
                            this.addHierarquiaMetricaCalculada((MetricaCalculadaMetaData) metaDataCampoOriginal);
                        }
                    }

                    if (CampoMetaData.METRICA_VISUALIZACAO_RESTRITA.equals(visualizacaoMetrica)) {
                        metaDataCampoOriginal.setVisualizada(false);
                    } else {
                        this.colunasVisualizadas.add(metaDataCampoOriginal);
                    }

                    if (campo.temAnaliseVertical()) {
                        List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricaCalculadaParticipacaoAVMetaData.AV);
                        MetricaCalculadaParticipacaoAVMetaData metricaAV = new MetricaCalculadaParticipacaoAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertasAV);
                        metricaAV.setTotalizarLinhas(true);
                        this.addHierarquiaMetricaCalculada(metricaAV);
                        this.colunasVisualizadas.add(metricaAV);
                    }
                    if (campo.temValorAcumulado()) {
                        List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricaCalculadaAcumuladoValorAVMetaData.VALOR_ACUMULADO_AV);
                        MetricaCalculadaAcumuladoValorAVMetaData metricaValorAcumuladoAV = new MetricaCalculadaAcumuladoValorAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertasAV);
                        this.addHierarquiaMetricaCalculada(metricaValorAcumuladoAV);
                        this.colunasVisualizadas.add(metricaValorAcumuladoAV);
                    }
                    if (campo.temParticipacaoAcumulada()) {
                        MetricaCalculadaParticipacaoMetaData metaDataAVAuxiliar = null;
                        if (!campo.temAnaliseVertical()) {
                            metaDataAVAuxiliar = new MetricaCalculadaParticipacaoAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, null);
                            metaDataAVAuxiliar.setVisualizada(false);
                            this.addHierarquiaMetricaCalculada(metaDataAVAuxiliar);
                        } else {
                            metaDataAVAuxiliar = (MetricaCalculadaParticipacaoMetaData) this.getMetricaMetaDataCampoRelativo(campo.getTituloCampo(), MetricaCalculadaParticipacaoAVMetaData.AV);
                        }
                        List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricaCalculadaAcumuladoParticipacaoAVMetaData.PARTICIPACAO_ACUMULADA_AV);
                        MetricaCalculadaAcumuladoParticipacaoAVMetaData metricaPartAcumAV = new MetricaCalculadaAcumuladoParticipacaoAVMetaData(metaDataAVAuxiliar, tipoAnaliseVerticalCampo, alertasAV);
                        this.addHierarquiaMetricaCalculada(metricaPartAcumAV);
                        this.colunasVisualizadas.add(metricaPartAcumAV);
                    }
                }
            }
        }

        List<MetricaCalculadaMetaData> metricasCalculadas = this.getHierarquiaMetricaCalculada();
        CalculoHierarquiaOrdenacao hierarquiaCalculo = new CalculoHierarquiaOrdenacao(metricasCalculadas);
        metricasCalculadas = hierarquiaCalculo.getMetricasCalculadasOrdenadas();
        this.setHierarquiaMetricaCalculada(metricasCalculadas);

        if (cuboMetaData.getExpressaoFiltrosMetrica() != null && !"".equals(cuboMetaData.getExpressaoFiltrosMetrica().trim())) {
            String expressaoFiltro = this.converteFiltroMetricaParaTitulosCampo(cuboMetaData.getExpressaoFiltrosMetrica());
            this.filtrosMetrica = new MetricFilters(expressaoFiltro, MetricaValorUtilizarLinhaMetrica.getInstance());
        }
        if (cuboMetaData.getExpressaoFiltrosAcumulado() != null && !"".equals(cuboMetaData.getExpressaoFiltrosAcumulado().trim())) {
            String expressaoFiltro = this.converteFiltroMetricaParaTitulosCampo(cuboMetaData.getExpressaoFiltrosAcumulado());
            this.filtrosMetricaAcumulado = new MetricFiltersAccumulatedValue(expressaoFiltro);
        }
    }

    @Override
    public void UpdateSequenceRanking(Iterator<Dimension> dimensionLine) {
        List<Dimension> lastLevelDimension = this.getDimensoesUltimoNivelLinha();
        int sequencia = 1;
        for (Dimension lastLine : lastLevelDimension) {
            lastLine.setRankingSequence(sequencia++);
        }
    }

    private void applyRanking(List<Dimension> dimensoes) {
        Iterator<Dimension> iDimensoes = dimensoes.iterator();
        ColunaMetaData firstColumn = this.getColunasVisualizadas().get(0);
        if (firstColumn.hasCampoSequencia() && firstColumn.getFunctionRanking() != null) {
            this.aplicaRanking(iDimensoes, firstColumn.getFunctionRanking(), dimensoes.size());
        }
    }

    @Override
    protected void verifyRanking() {
        this.applyRanking(this.getDimensoesUltimoNivelLinha());
    }

    @Override
    protected void removeDimensionsFiltersFunction(Dimension dimensionAdicionarOutros, List<Dimension> dimensoesRemover) {
        List<Dimension> lastLevelDimension = this.getDimensoesUltimoNivelLinha();
        for (Dimension dimensionLinhaRemover : dimensoesRemover) {
            Dimension dimensionPai = dimensionLinhaRemover.getParent();
            dimensionPai.removeDimensionLine(dimensionLinhaRemover);
            this.mapaMetricas.removeMetricLine(dimensionLinhaRemover);
            lastLevelDimension.remove(dimensionLinhaRemover);
        }
    }

    @Override
    protected void reorderData(List<OrdenacaoMetrica> orderedMetrics) {
        if (!orderedMetrics.isEmpty()) {
            if (this.lastOrderedDimension == null) {
                this.getDimensoesUltimoNivelLinha().sort(new DimensaoMetricaComparator(this.mapaMetricas, new ArrayList<>(), orderedMetrics));
            } else {
                List<Dimension> lDimensoesPaiReordenar = new ArrayList<>();
                this.buildDimensionReorderChildrenList(this, lDimensoesPaiReordenar);
                for (Dimension dimensionPaiReordenar : lDimensoesPaiReordenar) {
                    List<Dimension> dimensoesLinhaAux = new ArrayList<>();
                    this.geraListaUltimoNivel(dimensionPaiReordenar.getDimensionsLine().values(), dimensoesLinhaAux);
                    dimensoesLinhaAux.sort(new DimensaoMetricaComparator(this.mapaMetricas, new ArrayList<>(), orderedMetrics));
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
    protected void adidionaOrdenacoesMetrica(List<OrdenacaoMetrica> ordenacoesMetrica, List<OrdenacaoMetrica> listaOrdenacoes) {
        listaOrdenacoes.addAll(ordenacoesMetrica);
    }

}
