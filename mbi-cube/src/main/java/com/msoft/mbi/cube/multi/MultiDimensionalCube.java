package com.msoft.mbi.cube.multi;

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
import com.msoft.mbi.cube.multi.dimension.DimensionLineOutros;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensaoOutrosMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimensions;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoMetricaComparator;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFilters;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFiltersAccumulatedValue;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;
import com.msoft.mbi.cube.multi.metaData.CubeMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaValorUtilizarLinhaMetrica;
import com.msoft.mbi.cube.multi.metrics.MetricOrdering;
import com.msoft.mbi.cube.multi.metrics.additive.MetricAdditiveMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.CalculoHierarquiaOrdenacao;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAcumuladoParticipacaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAcumuladoParticipacaoAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAcumuladoValorAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedEvolucaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoMetaData;

public class MultiDimensionalCube extends Cube {

    public MultiDimensionalCube(CubeMetaData metaData) {
        super(metaData);
    }

    private void factoryColorsAlertOtherFields(MetricMetaData metricMetaData, List<ColorAlertMetadata> alerts, boolean relatedFieldFunction) {
        ColorAlertProperties propriedadeAlerta;
        ColorAlertConditionsMetricaOutroCampo condicaoAlertaCores;
        for (ColorAlertMetadata colorAlert : alerts) {
            propriedadeAlerta = ColorAlertProperties.factory(colorAlert.getFontColor(), colorAlert.getBackGroundColor(), colorAlert.getFontStyle(), colorAlert.isBold(), colorAlert.isItalic(), colorAlert.getFontSize());
            propriedadeAlerta.setAlignment(ColorAlertProperties.ALINHAMENTO_DIREITA);
            MetricMetaData outroCampo;
            if (!relatedFieldFunction) {
                outroCampo = getMetricByTitle(colorAlert.getSecondFieldTitle());
            } else {
                outroCampo = getMetricMetaDataRelativeField(colorAlert.getSecondFieldTitle(), colorAlert.getDestineFieldFunction());
            }
            if (outroCampo != null) {
                condicaoAlertaCores = new ColorAlertConditionsMetricaOutroCampo(colorAlert.getSequence(), propriedadeAlerta, colorAlert.getFunction(), colorAlert.getAction(), colorAlert.getOperator(), metricMetaData, colorAlert.getTypeComparison(), colorAlert.getReferenceValue(), outroCampo, colorAlert.getDestineFieldFunction());
                metricMetaData.addColorAlert(condicaoAlertaCores);
            }
        }
    }

    private AnaliseParticipacaoTipo getAVType(String type) {
        if (MetaDataField.AV_TYPE_PARTIAL_NEXT_LEVEL.equals(type)) {
            return AnaliseParticipacaoTipoParcialProxNivel.getInstance();
        } else if (MetaDataField.AV_TYPE_PARTIAL_NEXT_LEVEL_TOTAL.equals(type)) {
            return AnaliseParticipacaoTipoParcialProxNivelTotalizado.getInstance();
        } else {
            return AnaliseParticipacaoTipoGeral.getInstance();
        }
    }

    @Override
    protected void factory() {
        CubeMetaData cubeMetaData = (CubeMetaData) this.metaData;
        cubeMetaData.orderFields();
        cubeMetaData.orderDimensionFields();
        cubeMetaData.orderMetricFields();
        DimensaoMetaData dimensao;
        List<MetaDataField> camposMetrica = new ArrayList<>();
        for (MetaDataField campoDimensao : cubeMetaData.getDimensionFields()) {
            if ("S".equals(campoDimensao.getDefaultField())) {
                dimensao = DimensaoMetaData.factory(campoDimensao);
                if (MetaDataField.LINE == campoDimensao.getDisplayLocation()) {
                    this.addHierarchyLine(dimensao);
                } else {
                    this.addHierarchyColumn(dimensao);
                }
                this.columnsViewed.add(dimensao);
            }
        }
        AnaliseParticipacaoTipo tipoAnaliseVerticalCampo = AnaliseParticipacaoTipoGeral.getInstance();
        AnaliseParticipacaoTipo tipoAnaliseHorizontalCampo = AnaliseParticipacaoTipoGeral.getInstance();
        for (MetaDataField campo : cubeMetaData.getMetricFields()) {
            camposMetrica.add(campo);
            String visualizacaoMetrica = this.getMetricVisualizationStatus(campo);
            if (!MetaDataField.NOT_ADDED_METRIC.equals(visualizacaoMetrica)) {
                MetricMetaData metaDataCampoOriginal;
                if (!campo.isExpression()) {
                    metaDataCampoOriginal = MetricAdditiveMetaData.factory(campo);
                    this.addHierarchyLineMetricAdditive((MetricAdditiveMetaData) metaDataCampoOriginal);
                } else {
                    campo.setName(this.convertConditionalExpression(campo.getName()));

                    metaDataCampoOriginal = MetricCalculatedMetaData.factory(campo);

                    Double valorExpr = null;
                    Double resultExpr = null;

                    try {
                        resultExpr = ((MetricCalculatedMetaData) metaDataCampoOriginal).createCalculo().calculaValor();
                        valorExpr = Double.valueOf(campo.getName());
                    } catch (Exception e) {
                    }

                    if (resultExpr != null && resultExpr.equals(valorExpr)) {
                        metaDataCampoOriginal = MetricAdditiveMetaData.factory(campo);
                        this.addHierarchyLineMetricAdditive((MetricAdditiveMetaData) metaDataCampoOriginal);
                    } else {
                        this.addHierarchyLineMetricCalculated((MetricCalculatedMetaData) metaDataCampoOriginal);
                    }
                }
                this.columnsViewed.add(metaDataCampoOriginal);

                if (MetaDataField.METRIC_RESTRICTED_VIEW.equals(visualizacaoMetrica)) {
                    metaDataCampoOriginal.setViewed(false);
                }

                if (campo.isTotalLineField()) {
                    if (metaDataCampoOriginal.isViewed()) {
                        this.metricsTotalHorizontal.add(metaDataCampoOriginal);
                    }
                }

                if (campo.hasVerticalAnalysis()) {
                    tipoAnaliseVerticalCampo = this.getAVType(campo.getVerticalAnalysisType());
                    List<ColorAlertMetadata> alertsAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedParticipacaoAVMetaData.AV);
                    MetricCalculatedParticipacaoAVMetaData metricAV = new MetricCalculatedParticipacaoAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertsAV);
                    metricAV.setTotalLines(false);
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(metricAV);
                        this.columnsViewed.add(metricAV);
                    }
                }
                if (campo.hasAccumulatedValue()) {
                    List<ColorAlertMetadata> alertasAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAcumuladoValorAVMetaData.VALOR_ACUMULADO_AV);
                    MetricCalculatedAcumuladoValorAVMetaData metricaAcumuladoValorAV = new MetricCalculatedAcumuladoValorAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, alertasAV);
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(metricaAcumuladoValorAV);
                        this.columnsViewed.add(metricaAcumuladoValorAV);
                    }
                }
                if (campo.hasAccumulatedParticipation()) {
                    MetricCalculatedParticipacaoMetaData metaDataAVAuxiliar;
                    if (!campo.hasVerticalAnalysis()) {
                        metaDataAVAuxiliar = new MetricCalculatedParticipacaoAVMetaData(metaDataCampoOriginal, tipoAnaliseVerticalCampo, null);
                        metaDataAVAuxiliar.setViewed(false);
                        if (metaDataCampoOriginal.isViewed()) {
                            this.addHierarchyLineMetricCalculated(metaDataAVAuxiliar);
                        }
                    } else {
                        metaDataAVAuxiliar = (MetricCalculatedParticipacaoMetaData) this.getMetricMetaDataRelativeField(campo.getTitle(), MetricCalculatedParticipacaoAVMetaData.AV);
                    }
                    List<ColorAlertMetadata> alertasAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAcumuladoParticipacaoAVMetaData.PARTICIPACAO_ACUMULADA_AV);
                    if (metaDataCampoOriginal.isViewed()) {
                        MetricCalculatedAcumuladoParticipacaoAVMetaData metricaPartAcumAV = new MetricCalculatedAcumuladoParticipacaoAVMetaData(metaDataAVAuxiliar, tipoAnaliseVerticalCampo, alertasAV);
                        this.addHierarchyLineMetricCalculated(metricaPartAcumAV);
                        this.columnsViewed.add(metricaPartAcumAV);
                    }
                }

                if (campo.hasHorizontalAnalysis()) {
                    MetricCalculatedMetaData metaData;
                    List<ColorAlertMetadata> alertasAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedEvolucaoAHMetaData.AH);
                    if (MetaDataField.AH_TYPE_DYNAMIC.equals(campo.getHorizontalAnalysisType())) {
                        metaData = new MetricCalculatedEvolucaoAHMetaData(metaDataCampoOriginal, alertasAV, AnaliseEvolucaoTipoDinamica.getInstance());
                    } else {
                        metaData = new MetricCalculatedEvolucaoAHMetaData(metaDataCampoOriginal, alertasAV, AnaliseEvolucaoTipoFixa.getInstance());
                    }
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(metaData);
                        this.columnsViewed.add(metaData);
                    }
                }
                if (campo.hasHorizontalParticipation()) {
                    List<ColorAlertMetadata> alertasAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedParticipacaoAHMetaData.PARTICIPACAO_AH);
                    MetricCalculatedParticipacaoAHMetaData metricaPartAH = new MetricCalculatedParticipacaoAHMetaData(metaDataCampoOriginal, tipoAnaliseHorizontalCampo, alertasAV);
                    if (metaDataCampoOriginal.isViewed()) {
                        this.addHierarchyLineMetricCalculated(metricaPartAH);
                        this.columnsViewed.add(metricaPartAH);
                    }
                }
                if (campo.hasHorizontalAccumulatedParticipation()) {
                    MetricCalculatedParticipacaoAHMetaData metaDataAHAuxiliar;
                    if (!campo.hasHorizontalParticipation()) {
                        metaDataAHAuxiliar = new MetricCalculatedParticipacaoAHMetaData(metaDataCampoOriginal, tipoAnaliseHorizontalCampo, null);
                        metaDataAHAuxiliar.setViewed(false);
                        this.addHierarchyLineMetricCalculated(metaDataAHAuxiliar);
                    } else {
                        metaDataAHAuxiliar = (MetricCalculatedParticipacaoAHMetaData) this.getMetricMetaDataRelativeField(campo.getTitle(), MetricCalculatedParticipacaoAHMetaData.PARTICIPACAO_AH);
                    }
                    List<ColorAlertMetadata> alertasAV = campo.getRelativeFieldFunctionValueAlert(MetricCalculatedAcumuladoParticipacaoAHMetaData.PARTICIPACAO_ACUMULADA_AH);
                    if (metaDataCampoOriginal.isViewed()) {
                        MetricCalculatedAcumuladoParticipacaoAHMetaData metricaPartAcumAH = new MetricCalculatedAcumuladoParticipacaoAHMetaData(metaDataAHAuxiliar, tipoAnaliseHorizontalCampo, alertasAV);
                        this.addHierarchyLineMetricCalculated(metricaPartAcumAH);
                        this.columnsViewed.add(metricaPartAcumAH);
                    }
                }
            }
        }

        List<MetricCalculatedMetaData> metricasCalculadas = this.getHierarchyMetricCalculated();
        CalculoHierarquiaOrdenacao hierarquiaCalculo = new CalculoHierarquiaOrdenacao(metricasCalculadas);
        metricasCalculadas = hierarquiaCalculo.getMetricasCalculadasOrdenadas();
        this.setHierarchyMetricCalculated(metricasCalculadas);

        for (MetaDataField campo : camposMetrica) {
            if (!campo.getColorAlertMetadataSecondField().isEmpty()) {
                MetricMetaData metaData = this.getMetricByTitle(campo.getTitle());
                if (metaData != null) {
                    this.factoryColorsAlertOtherFields(metaData, campo.getColorAlertMetadataSecondField(), false);
                }
            }
            if (campo.hasVerticalAnalysis()) {
                List<ColorAlertMetadata> alertasAV = campo.getRelativeSecondFieldFunctionValueAlert(MetricCalculatedParticipacaoAVMetaData.AV);
                if (alertasAV != null) {
                    MetricMetaData campoFuncaoReferencia = this.getMetricMetaDataRelativeField(campo.getTitle(), MetricCalculatedParticipacaoAVMetaData.AV);
                    if (campoFuncaoReferencia != null) {
                        factoryColorsAlertOtherFields(campoFuncaoReferencia, alertasAV, true);
                    }
                }
            }
        }

        if (cubeMetaData.getMetricFieldsExpression() != null && !cubeMetaData.getMetricFieldsExpression().trim().isEmpty()) {
            String expressaoFiltro = this.convertMetricFilterToFieldTitle(cubeMetaData.getMetricFieldsExpression());
            this.metricFilters = new MetricFilters(expressaoFiltro, MetricaValorUtilizarLinhaMetrica.getInstance());
        }
        if (cubeMetaData.getAccumulatedFieldExpression() != null && !cubeMetaData.getAccumulatedFieldExpression().trim().isEmpty()) {
            String expressaoFiltro = this.convertMetricFilterToFieldTitle(cubeMetaData.getAccumulatedFieldExpression());
            this.metricFiltersAccumulatedValue = new MetricFiltersAccumulatedValue(expressaoFiltro);
        }

    }

    @Override
    protected void UpdateSequenceRanking(Iterator<Dimension> dimensionIterator) {
        int sequencia = 1;
        while (dimensionIterator.hasNext()) {
            Dimension dimensionLinha = dimensionIterator.next();
            if (!dimensionLinha.getDimensionsLine().isEmpty()) {
                UpdateSequenceRanking(dimensionLinha.getDimensionsLine().values().iterator());
            }
            dimensionLinha.setRankingSequence(sequencia++);
        }

    }

    private void applyRankings(Dimensions dimensions, Dimension dimensionPai) {
        Dimension primeiraDimension = (Dimension) dimensions.firstKey();
        Iterator<Dimension> iDimensoes = dimensions.values().iterator();
        if (primeiraDimension.getMetaData().hasSequenceFields() && primeiraDimension.getMetaData().getFunctionRanking() != null) {
            this.applyRanking(iDimensoes, primeiraDimension.getMetaData().getFunctionRanking(), dimensions.size());
        }
        iDimensoes = dimensionPai.getDimensionsLine().values().iterator();
        while (iDimensoes.hasNext()) {
            primeiraDimension = iDimensoes.next();
            if (!primeiraDimension.getDimensionsLine().isEmpty()) {
                this.applyRankings(primeiraDimension.getDimensionsLine(), primeiraDimension);
            }
        }
    }

    @Override
    protected void verifyRanking() {
        this.applyRankings(this.dimensionsLine, this);
    }

    @Override
    protected void removeDimensionsFiltersFunction(Dimension dimensionAdicionarOutros, List<Dimension> dimensions) {
        List<Dimension> lDimensoesLinhaUltimoNivel = this.getDimensionsLastLevelLines();
        DimensaoOutrosMetaData metaDataOutros = new DimensaoOutrosMetaData(dimensionAdicionarOutros.getMetaData().getFilho());
        DimensionLineOutros dimensaoOutros = new DimensionLineOutros(dimensionAdicionarOutros, metaDataOutros);
        for (Dimension dimensionLinhaRemover : dimensions) {
            dimensaoOutros.processar(dimensionLinhaRemover);
            dimensionLinhaRemover.getParent().removeDimensionLine(dimensionLinhaRemover);
            this.metricsMap.removeMetricLine(dimensionLinhaRemover);
            lDimensoesLinhaUltimoNivel.remove(dimensionLinhaRemover);
        }
    }

    @Override
    protected void reorderData(List<MetricOrdering> metricOrderings) {
        if (!metricOrderings.isEmpty()) {
            this.getLastMetaDataLine().setComparator(new DimensaoMetricaComparator(this.metricsMap, this.getDimensionsLastLevelColumns(), metricOrderings));
            this.reordenaDimensoesLinhaUltimoNivel(metricOrderings);
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

    protected void addMetricOrdering(List<MetricOrdering> metricOrderings, List<MetricOrdering> orderingList) {
        for (MetricOrdering metricOrdering : metricOrderings) {
            if (metricOrdering.getOrderingSequence() > this.getLastMetaDataLine().getSequenciaOrdenacao()) {
                orderingList.add(metricOrdering);
            }
        }
    }

    protected void reordenaDimensoesLinhaUltimoNivel(List<MetricOrdering> metricasOrdenadas) {
        List<Dimension> lDimensoesColunaUltimoNivel = this.getDimensionsLastLevelColumns();
        List<Dimension> lDimensoesPaiReordenar = new ArrayList<>();
        this.geraListaDimensoesReordenarFilhas(this, lDimensoesPaiReordenar);
        List<Dimension> dimensoes;
        for (Dimension dimensionPaiReordenar : lDimensoesPaiReordenar) {
            dimensoes = new ArrayList<>(dimensionPaiReordenar.getDimensionsLine().values());
            dimensionPaiReordenar.resetDimensionsLines(new DimensaoMetricaComparator(this.metricsMap, lDimensoesColunaUltimoNivel, metricasOrdenadas));
            for (Dimension dimension : dimensoes) {
                dimensionPaiReordenar.addDimensionLine(dimension);
                Dimension.increaseTotalSize(dimensionPaiReordenar);
            }
        }
    }

}
