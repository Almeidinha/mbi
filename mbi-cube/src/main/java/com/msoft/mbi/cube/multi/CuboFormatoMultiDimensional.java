package com.msoft.mbi.cube.multi;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnaliseEvolucaoTipoDinamica;
import com.msoft.mbi.cube.multi.analytics.AnaliseEvolucaoTipoFixa;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoGeral;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoParcialProxNivel;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoParcialProxNivelTotalizado;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetricaOutroCampo;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertProperties;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionLinhaOutros;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensaoOutrosMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimensions;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoMetricaComparator;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFilters;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFiltersAccumulatedValue;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metaData.CuboMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizarLinhaMetrica;
import com.msoft.mbi.cube.multi.metrics.OrdenacaoMetrica;
import com.msoft.mbi.cube.multi.metrics.additive.MetricaAditivaMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.CalculoHierarquiaOrdenacao;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaAcumuladoParticipacaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaAcumuladoParticipacaoAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaAcumuladoValorAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaEvolucaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaParticipacaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaParticipacaoAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaParticipacaoMetaData;

public class CuboFormatoMultiDimensional extends Cubo {

    @Serial
    private static final long serialVersionUID = -9102046413842085221L;

    public static final String FWJ_VERSAO = "$Revision: 1.6 $";

    public CuboFormatoMultiDimensional(CuboMetaData metaData) {
        super(metaData);
    }

    private void factoryAlertasCoresOutroCampo(MetricaMetaData metricaMetaData, List<AlertaCorMetaData> alertas, boolean isFuncaoCampoRelativo) {
        ColorAlertProperties propriedadeAlerta;
        ColorAlertConditionsMetricaOutroCampo condicaoAlertaCores;
        for (AlertaCorMetaData alertaCor : alertas) {
            propriedadeAlerta = ColorAlertProperties.factory(alertaCor.getCorFonte(), alertaCor.getCorFundo(), alertaCor.getEstiloFonte(), alertaCor.isNegrito(), alertaCor.isItalico(), alertaCor.getTamanhoFonte());
            propriedadeAlerta.setAlignment(ColorAlertProperties.ALINHAMENTO_DIREITA);
            MetricaMetaData outroCampo;
            if (!isFuncaoCampoRelativo) {
                outroCampo = getMetricaMetaDataByTitulo(alertaCor.getTituloOutroCampo());
            } else {
                outroCampo = getMetricaMetaDataCampoRelativo(alertaCor.getTituloOutroCampo(), alertaCor.getFuncaoCampoDestino());
            }
            if (outroCampo != null) {
                condicaoAlertaCores = new ColorAlertConditionsMetricaOutroCampo(alertaCor.getSequencia(), propriedadeAlerta, alertaCor.getFuncao(), alertaCor.getAcao(), alertaCor.getOperador(), metricaMetaData, alertaCor.getTipoComparacao(), alertaCor.getValorReferencia(), outroCampo, alertaCor.getFuncaoCampoDestino());
                metricaMetaData.addAlertaCor(condicaoAlertaCores);
            }
        }
    }

    private AnaliseParticipacaoTipo getTipoAV(String tipoAV) {
        if (CampoMetaData.TIPO_AV_PARCIAL_PROX_NIVEL.equals(tipoAV)) {
            return AnaliseParticipacaoTipoParcialProxNivel.getInstance();
        } else if (CampoMetaData.TIPO_AV_PARCIAL_PROX_NIVEL_TOTALIZADO.equals(tipoAV)) {
            return AnaliseParticipacaoTipoParcialProxNivelTotalizado.getInstance();
        } else {
            return AnaliseParticipacaoTipoGeral.getInstance();
        }
    }

    @Override
    protected void factory() {
        CuboMetaData cuboMetaData = (CuboMetaData) this.metaData;
        cuboMetaData.ordenaCampos();
        cuboMetaData.ordenaCamposDimensao();
        cuboMetaData.ordenaCamposMetrica();
        DimensaoMetaData dimensao = null;
        List<CampoMetaData> camposMetrica = new ArrayList<>();
        for (CampoMetaData campoDimensao : cuboMetaData.getCamposDimensao()) {
            if ("S".equals(campoDimensao.getPadrao())) {
                dimensao = DimensaoMetaData.factory(campoDimensao);
                if (CampoMetaData.LINHA == campoDimensao.getLocalApresentacao()) {
                    this.addHierarquiaLinha(dimensao);
                } else {
                    this.addHierarquiaColuna(dimensao);
                }
                this.colunasVisualizadas.add(dimensao);
            }
        }
        AnaliseParticipacaoTipo tipoAnaliseVerticalCampo = AnaliseParticipacaoTipoGeral.getInstance();
        AnaliseParticipacaoTipo tipoAnaliseHorizontalCampo = AnaliseParticipacaoTipoGeral.getInstance();
        for (CampoMetaData campo : cuboMetaData.getCamposMetrica()) {
            camposMetrica.add(campo);
            String visualizacaoMetrica = this.getStatusVisualizacaoMetrica(campo);
            if (!CampoMetaData.METRICA_NAO_ADICIONADA.equals(visualizacaoMetrica)) {
                MetricaMetaData metaDataCampoOriginal = null;
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
                this.colunasVisualizadas.add(metaDataCampoOriginal);

                if (CampoMetaData.METRICA_VISUALIZACAO_RESTRITA.equals(visualizacaoMetrica)) {
                    metaDataCampoOriginal.setVisualizada(false);
                }

                if (campo.isTotalizaCampoLinha()) {
                    if (metaDataCampoOriginal.isVisualizada()) {
                        this.metricasTotalizaHorizontal.add(metaDataCampoOriginal);
                    }
                }

                if (campo.temAnaliseVertical()) {
                    tipoAnaliseVerticalCampo = this.getTipoAV(campo.getAnaliseVerticalTipo());
                    List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricaCalculadaParticipacaoAVMetaData.AV);
                    MetricaCalculadaParticipacaoAVMetaData metricaAV = new MetricaCalculadaParticipacaoAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertasAV);
                    metricaAV.setTotalizarLinhas(false);
                    if (metaDataCampoOriginal.isVisualizada()) {
                        this.addHierarquiaMetricaCalculada(metricaAV);
                        this.colunasVisualizadas.add(metricaAV);
                    }
                }
                if (campo.temValorAcumulado()) {
                    List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricaCalculadaAcumuladoValorAVMetaData.VALOR_ACUMULADO_AV);
                    MetricaCalculadaAcumuladoValorAVMetaData metricaAcumuladoValorAV = new MetricaCalculadaAcumuladoValorAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertasAV);
                    if (metaDataCampoOriginal.isVisualizada()) {
                        this.addHierarquiaMetricaCalculada(metricaAcumuladoValorAV);
                        this.colunasVisualizadas.add(metricaAcumuladoValorAV);
                    }
                }
                if (campo.temParticipacaoAcumulada()) {
                    MetricaCalculadaParticipacaoMetaData metaDataAVAuxiliar;
                    if (!campo.temAnaliseVertical()) {
                        metaDataAVAuxiliar = new MetricaCalculadaParticipacaoAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, null);
                        metaDataAVAuxiliar.setVisualizada(false);
                        if (metaDataCampoOriginal.isVisualizada()) {
                            this.addHierarquiaMetricaCalculada(metaDataAVAuxiliar);
                        }
                    } else {
                        metaDataAVAuxiliar = (MetricaCalculadaParticipacaoMetaData) this.getMetricaMetaDataCampoRelativo(campo.getTituloCampo(), MetricaCalculadaParticipacaoAVMetaData.AV);
                    }
                    List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricaCalculadaAcumuladoParticipacaoAVMetaData.PARTICIPACAO_ACUMULADA_AV);
                    if (metaDataCampoOriginal.isVisualizada()) {
                        MetricaCalculadaAcumuladoParticipacaoAVMetaData metricaPartAcumAV = new MetricaCalculadaAcumuladoParticipacaoAVMetaData(metaDataAVAuxiliar, tipoAnaliseVerticalCampo, alertasAV);
                        this.addHierarquiaMetricaCalculada(metricaPartAcumAV);
                        this.colunasVisualizadas.add(metricaPartAcumAV);
                    }
                }

                if (campo.temAnaliseHorizontal()) {
                    MetricaCalculadaMetaData metaData;
                    List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricaCalculadaEvolucaoAHMetaData.AH);
                    if (CampoMetaData.TIPO_AH_DINAMICA.equals(campo.getAnaliseHorizontalTipo())) {
                        metaData = new MetricaCalculadaEvolucaoAHMetaData(metaDataCampoOriginal, alertasAV, AnaliseEvolucaoTipoDinamica.getInstance());
                    } else {
                        metaData = new MetricaCalculadaEvolucaoAHMetaData(metaDataCampoOriginal, alertasAV, AnaliseEvolucaoTipoFixa.getInstance());
                    }
                    if (metaDataCampoOriginal.isVisualizada()) {
                        this.addHierarquiaMetricaCalculada(metaData);
                        this.colunasVisualizadas.add(metaData);
                    }
                }
                if (campo.temParticipacaoHorizontal()) {
                    List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricaCalculadaParticipacaoAHMetaData.PARTICIPACAO_AH);
                    MetricaCalculadaParticipacaoAHMetaData metricaPartAH = new MetricaCalculadaParticipacaoAHMetaData(metaDataCampoOriginal, tipoAnaliseHorizontalCampo, alertasAV);
                    if (metaDataCampoOriginal.isVisualizada()) {
                        this.addHierarquiaMetricaCalculada(metricaPartAH);
                        this.colunasVisualizadas.add(metricaPartAH);
                    }
                }
                if (campo.temParticipacaoAcumuladaHorizontal()) {
                    MetricaCalculadaParticipacaoAHMetaData metaDataAHAuxiliar;
                    if (!campo.temParticipacaoHorizontal()) {
                        metaDataAHAuxiliar = new MetricaCalculadaParticipacaoAHMetaData(metaDataCampoOriginal, tipoAnaliseHorizontalCampo, null);
                        metaDataAHAuxiliar.setVisualizada(false);
                        this.addHierarquiaMetricaCalculada(metaDataAHAuxiliar);
                    } else {
                        metaDataAHAuxiliar = (MetricaCalculadaParticipacaoAHMetaData) this.getMetricaMetaDataCampoRelativo(campo.getTituloCampo(), MetricaCalculadaParticipacaoAHMetaData.PARTICIPACAO_AH);
                    }
                    List<AlertaCorMetaData> alertasAV = campo.getAlertasValorFuncaoDeCampoRelativo(MetricaCalculadaAcumuladoParticipacaoAHMetaData.PARTICIPACAO_ACUMULADA_AH);
                    if (metaDataCampoOriginal.isVisualizada()) {
                        MetricaCalculadaAcumuladoParticipacaoAHMetaData metricaPartAcumAH = new MetricaCalculadaAcumuladoParticipacaoAHMetaData(metaDataAHAuxiliar, tipoAnaliseHorizontalCampo, alertasAV);
                        this.addHierarquiaMetricaCalculada(metricaPartAcumAH);
                        this.colunasVisualizadas.add(metricaPartAcumAH);
                    }
                }
            }
        }

        List<MetricaCalculadaMetaData> metricasCalculadas = this.getHierarquiaMetricaCalculada();
        CalculoHierarquiaOrdenacao hierarquiaCalculo = new CalculoHierarquiaOrdenacao(metricasCalculadas);
        metricasCalculadas = hierarquiaCalculo.getMetricasCalculadasOrdenadas();
        this.setHierarquiaMetricaCalculada(metricasCalculadas);

        for (CampoMetaData campo : camposMetrica) {
            if (campo.getAlertasCoresOutroCampo().size() != 0) {
                MetricaMetaData metaData = this.getMetricaMetaDataByTitulo(campo.getTituloCampo());
                if (metaData != null) {
                    this.factoryAlertasCoresOutroCampo(metaData, campo.getAlertasCoresOutroCampo(), false);
                }
            }
            if (campo.temAnaliseVertical()) {
                List<AlertaCorMetaData> alertasAV = campo.getAlertasOutroCampoFuncaoDeCampoRelativo(MetricaCalculadaParticipacaoAVMetaData.AV);
                if (alertasAV != null) {
                    MetricaMetaData campoFuncaoReferencia = this.getMetricaMetaDataCampoRelativo(campo.getTituloCampo(), MetricaCalculadaParticipacaoAVMetaData.AV);
                    if (campoFuncaoReferencia != null) {
                        factoryAlertasCoresOutroCampo(campoFuncaoReferencia, alertasAV, true);
                    }
                }
            }
        }

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
    protected void UpdateSequenceRanking(Iterator<Dimension> iDimensoesLinha) {
        int sequencia = 1;
        while (iDimensoesLinha.hasNext()) {
            Dimension dimensionLinha = iDimensoesLinha.next();
            if (!dimensionLinha.getDimensionsLine().isEmpty()) {
                UpdateSequenceRanking(dimensionLinha.getDimensionsLine().values().iterator());
            }
            dimensionLinha.setRankingSequence(sequencia++);
        }

    }

    private void aplicaRankings(Dimensions dimensions, Dimension dimensionPai) {
        Dimension primeiraDimension = (Dimension) dimensions.firstKey();
        Iterator<Dimension> iDimensoes = dimensions.values().iterator();
        if (primeiraDimension.getMetaData().hasCampoSequencia() && primeiraDimension.getMetaData().getFunctionRanking() != null) {
            this.aplicaRanking(iDimensoes, primeiraDimension.getMetaData().getFunctionRanking(), dimensions.size());
        }
        iDimensoes = dimensionPai.getDimensionsLine().values().iterator();
        while (iDimensoes.hasNext()) {
            primeiraDimension = iDimensoes.next();
            if (!primeiraDimension.getDimensionsLine().isEmpty()) {
                this.aplicaRankings(primeiraDimension.getDimensionsLine(), primeiraDimension);
            }
        }
    }

    @Override
    protected void verifyRanking() {
        this.aplicaRankings(this.dimensionsLine, this);
    }

    @Override
    protected void removeDimensionsFiltersFunction(Dimension dimensionAdicionarOutros, List<Dimension> dimensoesRemover) {
        List<Dimension> lDimensoesLinhaUltimoNivel = this.getDimensoesUltimoNivelLinha();
        DimensaoOutrosMetaData metaDataOutros = new DimensaoOutrosMetaData(dimensionAdicionarOutros.getMetaData().getFilho());
        DimensionLinhaOutros dimensaoOutros = new DimensionLinhaOutros(dimensionAdicionarOutros, metaDataOutros);
        for (Dimension dimensionLinhaRemover : dimensoesRemover) {
            dimensaoOutros.processar(dimensionLinhaRemover);
            dimensionLinhaRemover.getParent().removeDimensionLine(dimensionLinhaRemover);
            this.mapaMetricas.removeMetricLine(dimensionLinhaRemover);
            lDimensoesLinhaUltimoNivel.remove(dimensionLinhaRemover);
        }
    }

    @Override
    protected void reorderData(List<OrdenacaoMetrica> metricasOrdenadas) {
        if (!metricasOrdenadas.isEmpty()) {
            this.getLastMetaDataLinha().setComparator(new DimensaoMetricaComparator(this.mapaMetricas, this.getDimensoesUltimoNivelColuna(), metricasOrdenadas));
            this.reordenaDimensoesLinhaUltimoNivel(metricasOrdenadas);
        }
    }

    protected void geraListaDimensoesReordenarFilhas(Dimension dimensionPai, List<Dimension> dimensoesPai) {
        Iterator<Dimension> iDimensoes = dimensionPai.getDimensionsLine().values().iterator();
        Dimension dimension = null;
        while (iDimensoes.hasNext()) {
            dimension = iDimensoes.next();
            if (dimension.getMetaData().isUltima()) {
                dimensoesPai.add(dimensionPai);
                break;
            } else {
                geraListaDimensoesReordenarFilhas(dimension, dimensoesPai);
            }
        }
    }

    protected void adidionaOrdenacoesMetrica(List<OrdenacaoMetrica> ordenacoesMetrica, List<OrdenacaoMetrica> listaOrdenacoes) {
        for (OrdenacaoMetrica ordenacaoMetrica : ordenacoesMetrica) {
            if (ordenacaoMetrica.getSequenciaOrdenacao() > this.getLastMetaDataLinha().getSequenciaOrdenacao()) {
                listaOrdenacoes.add(ordenacaoMetrica);
            }
        }
    }

    protected void reordenaDimensoesLinhaUltimoNivel(List<OrdenacaoMetrica> metricasOrdenadas) {
        List<Dimension> lDimensoesColunaUltimoNivel = this.getDimensoesUltimoNivelColuna();
        List<Dimension> lDimensoesPaiReordenar = new ArrayList<>();
        this.geraListaDimensoesReordenarFilhas(this, lDimensoesPaiReordenar);
        List<Dimension> dimensoes;
        for (Dimension dimensionPaiReordenar : lDimensoesPaiReordenar) {
            dimensoes = new ArrayList<>(dimensionPaiReordenar.getDimensionsLine().values());
            dimensionPaiReordenar.resetDimensionsLines(new DimensaoMetricaComparator(this.mapaMetricas, lDimensoesColunaUltimoNivel, metricasOrdenadas));
            for (Dimension dimension : dimensoes) {
                dimensionPaiReordenar.addDimensionLine(dimension);
                Dimension.increaseTotalSize(dimensionPaiReordenar);
            }
        }
    }

}
