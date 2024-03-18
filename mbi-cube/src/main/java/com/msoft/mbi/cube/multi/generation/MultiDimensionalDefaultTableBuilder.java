package com.msoft.mbi.cube.multi.generation;

import java.util.*;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditions;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionColunaNula;
import com.msoft.mbi.cube.multi.dimension.DimensionLinha;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimensions;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaEvolucaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaFuncaoMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class MultiDimensionalDefaultTableBuilder extends tableGenerator {
    private final List<MetricaMetaData> metricasTotalizaSomaColunas;
    private List<MetricaMetaData> metricasTotalizaSomaGeralColunas;
    private final List<MetricaMetaData> metricasTotalizaMediaColunas;
    private final List<MetricaMetaData> metricasAH;
    private ImpressaoMetricaLinhaAtual impressaoMetricaLinha = null;
    private DimensionColunaNula dimensaoColunaNula = null;
    private String alertaMetricaLinhaAtual;
    private List<Dimension> dimensoesColunaUltimoNivel = null;

    private Dimension dimensionLinhaAnterior = null;

    public MultiDimensionalDefaultTableBuilder(Cubo cubo) {
        this.cube = cubo;
        dimensaoColunaNula = new DimensionColunaNula(cubo);
        this.metricsAmount = 0;
        this.visibleMetrics = new ArrayList<>();
        this.metricasTotalizaSomaColunas = new ArrayList<>();
        this.metricasTotalizaSomaGeralColunas = new ArrayList<>();
        this.metricasTotalizaMediaColunas = new ArrayList<>();
        this.metricasAH = new ArrayList<>();
        this.dimensoesColunaUltimoNivel = this.cube.getDimensoesUltimoNivelColuna();
        this.populaMetricasVisualizadas();
        this.impressaoMetricaLinha = new ImpressaoMetricaLinhaAtual(this.visibleMetrics);
    }

    private void processarTabelaSemDados() {
        this.impressor.iniciaImpressao();
        this.openLine();
        this.impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_DIMENSAO, "A pesquisa não retornou dados");
        this.impressor.fechaLinha();
        this.impressor.finalizaImpressao();
    }

    public void processar(Impressor iImpressor) {
        this.impressor = iImpressor;
        if (!cube.getDimensionsLine().isEmpty()) {
            this.createDefaultStyles();
            this.impressor.iniciaImpressao();
            this.impressor.abreLinhaHead();
            int nivelImpressao = 0;
            int colspanNiveisLinhaComSequencia = this.cube.getMetaData().getQtdNiveisAbaixoComSequencia();
            int expansaoMaximaColuna = this.cube.getHierarquiaColuna().size();
            int verificaProcessamento = 0;
            for (DimensaoMetaData metadata : this.cube.getHierarquiaColuna()) {
                verificaProcessamento++;
                if (verificaProcessamento % 100 == 0) {
                    if (this.cube.getCuboListener().stopProcess())
                        return;
                }
                this.openLine();
                this.impressor.imprimeCabecalhoColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_DIMENSAO, metadata, colspanNiveisLinhaComSequencia, 1);

                if (!this.cube.getDimensionsColumn().isEmpty()) {
                    Collection<Dimension> set = this.cube.getDimensionsColumn().values();
                    Iterator<Dimension> it = set.iterator();
                    this.mergulhaNivelColuna(it, nivelImpressao, CellProperty.PROPRIEDADE_CELULA_VALOR_DIMENSAO);
                }

                nivelImpressao++;
                if (nivelImpressao == 1) {
                    this.imprimeCabecalhoTotalizadoresColunas();
                    this.imprimeCabecalhoTotalGeralMetricasColunas();
                } else if (nivelImpressao == expansaoMaximaColuna) {
                    break;
                }
                this.impressor.fechaLinha();
            }

            this.openLine();
            this.imprimeCabecalhoDimensoesLinha();

            this.imprimeCabecalhoMetricas(this.cube);
            this.imprimeCabecalhoMetricasTotalizadoresColunas();

            this.impressor.fechaLinha();
            this.impressor.fechaLinhaHead();

            this.impressor.abreLinhaBody();
            if (!this.cube.getDimensionsLine().isEmpty()) {
                Collection<Dimension> set = ((Dimensions) this.cube.getDimensionsLine().clone()).values();
                Iterator<Dimension> it = set.iterator();
                this.openLine();
                this.mergulhaNivelLinha(it, null);
                nivelImpressao = 0;
                this.imprimeLinhaTotalGeralLinhas(colspanNiveisLinhaComSequencia);
            }
            nivelImpressao++;
            this.impressor.fechaLinhaBody();
            this.impressor.finalizaImpressao();

        } else {
            this.impressor.setCorBordasPadrao("3377CC");
            criaEstiloCabecalhoDimensao();
            this.processarTabelaSemDados();
        }
    }

    private void criaEstilosAlertasDeCoresComLink(List<?> alertasCores) {
        for (Object oAlertaCores : alertasCores) {
            ColorAlertConditions alertaCores = (ColorAlertConditions) oAlertaCores;
            this.impressor.adicionaEstilo(alertaCores.getAlertProperty(), CellProperty.PROPRIEDADE_CELULA_ALERTAS_PREFIXO + alertaCores.getSequence());
            CellProperty cellProperty = new CellProperty();
            cellProperty.addExtraAttributes("cursor", "pointer");
            this.impressor.adicionaEstiloLink(cellProperty, CellProperty.PROPRIEDADE_CELULA_ALERTAS_PREFIXO + alertaCores.getSequence() + " span");
        }
    }

    protected void openLine() {
        super.openLine();
        this.alertaMetricaLinhaAtual = null;
    }

    private void criaEstilosAlertasDeCores() {
        for (MetricaMetaData metaData : this.cube.getHierarquiaMetrica()) {
            this.createColorAlertStyles(metaData.getAlertasCoresCelula());
            this.createColorAlertStyles(metaData.getAlertasCoresLinha());
        }
        for (DimensaoMetaData metaData : this.cube.getHierarquiaColuna()) {
            this.createColorAlertStyles(metaData.getAlertasCoresCelula());
            this.createColorAlertStyles(metaData.getAlertasCoresLinha());
        }
        for (DimensaoMetaData metaData : this.cube.getHierarquiaLinha()) {
            this.criaEstilosAlertasDeCoresComLink(metaData.getAlertasCoresCelula());
            this.criaEstilosAlertasDeCoresComLink(metaData.getAlertasCoresLinha());
        }
    }

    private void criaEstiloCabecalhoDimensao() {
        CellProperty cellProperty = new CellProperty();
        cellProperty.setFontColor("FFFFFF");
        cellProperty.setBackGroundColor("3377CC");
        cellProperty.setFontName("Verdana");
        cellProperty.setBold(true);
        cellProperty.setFontSize(10);
        cellProperty.setSpecificBorder(true);
        cellProperty.setBorderColor("000000");

        this.impressor.adicionaEstiloCabecalhoColuna(cellProperty, CellProperty.PROPRIEDADE_CELULA_CABECALHO_DIMENSAO);
    }

    protected void createDefaultStyles() {
        this.impressor.setCorBordasPadrao("3377CC");
        super.createDefaultStyles();
        CellProperty propriedadeDimensaoPadrao = new CellProperty();
        propriedadeDimensaoPadrao.setAlignment(CellProperty.ALINHAMENTO_ESQUERDA);
        propriedadeDimensaoPadrao.setFontColor("000080");
        propriedadeDimensaoPadrao.setBackGroundColor("A2C8E8");
        propriedadeDimensaoPadrao.setFontName("Verdana");
        propriedadeDimensaoPadrao.setFontSize(10);
        propriedadeDimensaoPadrao.setSpecificBorder(true);
        propriedadeDimensaoPadrao.setBorderColor("3377CC");
        propriedadeDimensaoPadrao.addExtraAttributes("border-top", "none");
        propriedadeDimensaoPadrao.addExtraAttributes("border-left", "none");
        this.impressor.adicionaEstilo(propriedadeDimensaoPadrao, CellProperty.PROPRIEDADE_CELULA_VALOR_DIMENSAO);

        CellProperty cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.impressor.adicionaEstiloLink(cellProperty, CellProperty.PROPRIEDADE_CELULA_VALOR_DIMENSAO + " span");

        cellProperty = new CellProperty();
        cellProperty.setAlignment(CellProperty.ALINHAMENTO_DIREITA);
        cellProperty.setFontColor("000080");
        cellProperty.setBackGroundColor("CCCCCC");
        cellProperty.setFontName("Verdana");
        cellProperty.setFontSize(10);
        cellProperty.setBold(true);
        //propriedadeCelula.addOutroAtributo("white-space", "nowrap");
        cellProperty.setBorderColor("3377CC");
        cellProperty.setSpecificBorder(true);
        cellProperty.addExtraAttributes("border-top", "none");
        this.impressor.adicionaEstilo(cellProperty, CellProperty.PROPRIEDADE_CELULA_VALOR_TOTALPARCIALLINHAS);

        cellProperty = new CellProperty();
        cellProperty.setAlignment(CellProperty.ALINHAMENTO_ESQUERDA);
        cellProperty.setFontColor("000080");
        cellProperty.setBackGroundColor("CCCCCC");
        cellProperty.setFontName("Verdana");
        cellProperty.setFontSize(10);
        cellProperty.setBold(true);
        cellProperty.setBorderColor("3377CC");
        cellProperty.setSpecificBorder(true);
        cellProperty.addExtraAttributes("border-top", "none");
        this.impressor.adicionaEstilo(cellProperty, CellProperty.PROPRIEDADE_CELULA_CABECALHO_TOTALPARCIAL);

        cellProperty = new CellProperty();
        cellProperty.setAlignment(CellProperty.ALINHAMENTO_CENTRO);
        cellProperty.setFontColor("000080");
        cellProperty.setBackGroundColor("CCCCCC");
        cellProperty.setFontName("Verdana");
        cellProperty.setFontSize(10);
        cellProperty.setBold(true);
        cellProperty.setBorderColor("3377CC");
        cellProperty.setSpecificBorder(true);
        cellProperty.addExtraAttributes("border-top", "none");
        this.impressor.adicionaEstilo(cellProperty, CellProperty.PROPRIEDADE_CELULA_CABECALHO_TOTAISCOLUNAS);

        criaEstiloCabecalhoDimensao();

        cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.impressor.adicionaEstiloLink(cellProperty, CellProperty.PROPRIEDADE_CELULA_CABECALHO_DIMENSAO + " span");

        cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.impressor.adicionaEstiloLink(cellProperty, CellProperty.PROPRIEDADE_CELULA_CABECALHO_DIMENSAO + " IMG");

        CellProperty propriedadeCabecalhoMetrica = new CellProperty();
        propriedadeCabecalhoMetrica.setAlignment(CellProperty.ALINHAMENTO_CENTRO);
        propriedadeCabecalhoMetrica.setFontColor("000080");
        propriedadeCabecalhoMetrica.setBackGroundColor("A2C8E8");
        propriedadeCabecalhoMetrica.setFontName("Verdana");
        propriedadeCabecalhoMetrica.setBold(true);
        propriedadeCabecalhoMetrica.setFontSize(10);
        propriedadeCabecalhoMetrica.setSpecificBorder(true);
        propriedadeCabecalhoMetrica.setBorderColor("3377CC");
        this.impressor.adicionaEstiloCabecalhoColuna(propriedadeCabecalhoMetrica, CellProperty.PROPRIEDADE_CELULA_CABECALHO_METRICA);

        cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.impressor.adicionaEstiloLink(cellProperty, CellProperty.PROPRIEDADE_CELULA_CABECALHO_METRICA + " span");

        CellProperty cellPropertyValorMetricaOutros = new CellProperty();
        cellPropertyValorMetricaOutros.setAlignment(CellProperty.ALINHAMENTO_ESQUERDA);
        cellPropertyValorMetricaOutros.setFontColor("000080");
        cellPropertyValorMetricaOutros.setBackGroundColor("A2C8E8");
        cellPropertyValorMetricaOutros.setFontName("Verdana");
        cellPropertyValorMetricaOutros.setFontSize(10);
        //propriedadeCelulaValorMetricaOutros.addOutroAtributo("white-space", "nowrap");
        cellPropertyValorMetricaOutros.addExtraAttributes("border-left", "none");
        cellPropertyValorMetricaOutros.addExtraAttributes("border-right", "1px solid #3377CC");
        cellPropertyValorMetricaOutros.setBorderColor("3377CC");
        cellPropertyValorMetricaOutros.setSpecificBorder(true);
        this.impressor.adicionaEstilo(cellPropertyValorMetricaOutros, CellProperty.PROPRIEDADE_CELULA_OUTROS);

        this.criaEstilosAlertasDeCores();

        super.createsSpecificStylesColumns();
    }

    private void populaMetricasVisualizadas() {
        boolean temDimensaoColuna = !this.cube.getHierarquiaColuna().isEmpty();
        this.metricasTotalizaSomaGeralColunas = this.cube.getMetricasTotalizacaoHorizontal();
        int verificaProcessamento = 0;
        for (MetricaMetaData metaData : this.cube.getHierarquiaMetrica()) {
            verificaProcessamento++;
            if (verificaProcessamento % 100 == 0) {
                if (this.cube.getCuboListener().stopProcess())
                    return;
            }
            if (metaData.isVisualizada()) {
                if (metaData.isTotalizarSomaColunas()) {
                    this.metricasTotalizaSomaColunas.add(metaData);
                }
                if (metaData.isTotalizarMediaColunas()) {
                    this.metricasTotalizaMediaColunas.add(metaData);
                }

                if (metaData instanceof MetricaCalculadaFuncaoMetaData metricaDeAnalise) {
                    if (metricaDeAnalise.getFuncaoCampo().equals(MetricaCalculadaEvolucaoAHMetaData.AH)) {
                        if (temDimensaoColuna) {
                            this.metricasAH.add(metaData);
                            this.visibleMetrics.add(metaData);
                        }
                    } else {
                        this.visibleMetrics.add(metaData);
                    }
                } else {
                    this.visibleMetrics.add(metaData);
                }
                this.metricsAmount++;
            }
        }
    }

    private void imprimeCabecalhoTotalizadoresColunas() {
        if (!this.metricasTotalizaSomaColunas.isEmpty()) {
            this.impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_TOTAISCOLUNAS, "Acumulado", this.metricasTotalizaSomaColunas.size(), this.cube.getHierarquiaColuna().size());
        }
        if (!this.metricasTotalizaMediaColunas.isEmpty()) {
            this.impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_TOTAISCOLUNAS, "M�dia", this.metricasTotalizaMediaColunas.size(), this.cube.getHierarquiaColuna().size());
        }
    }

    private void imprimeCabecalhoTotalGeralMetricasColunas() {
        if (!this.metricasTotalizaSomaGeralColunas.isEmpty()) {
            this.impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_TOTAISCOLUNAS, "Total", 1, this.cube.getHierarquiaColuna().size() + 1);
        }
    }

    private void imprimeLinhaTotalGeralLinhas(int colspanNiveisLinhaComSequencia) {
        this.openLine();
        this.impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_TOTALGERAL, this.impressor.getValorVazio(), colspanNiveisLinhaComSequencia, 1);
        List<String> funcoesAlerta = new ArrayList<>();
        funcoesAlerta.add(MetricaMetaData.TOTALIZACAO_GERAL);

        String propriedadeTotalGeral = CellProperty.PROPRIEDADE_CELULA_TOTALGERAL;
        String propriedadeAlertaCoresMetricaLinhaFuncoes = this.cube.searchMetricsPropertyAlertsRowFunctionsTotalColumns(this.visibleMetrics, funcoesAlerta);
        if (propriedadeAlertaCoresMetricaLinhaFuncoes != null) {
            propriedadeTotalGeral = propriedadeAlertaCoresMetricaLinhaFuncoes;
            this.alertaMetricaLinhaAtual = propriedadeAlertaCoresMetricaLinhaFuncoes;
        }
        funcoesAlerta.add(MetricaMetaData.TOTALIZACAO_AV);
        this.imprimeMetricas(this.cube, propriedadeTotalGeral, new ImpressaoMetricaLinhaTotalizacaoLinhas(this.visibleMetrics, funcoesAlerta), funcoesAlerta, MetricaMetaData.TOTALIZACAO_AV);
        this.impressor.fechaLinha();
    }

    private List<MetricaMetaData> getMetricasSemAH(List<MetricaMetaData> metricasImprimirPadrao) {
        List<MetricaMetaData> metricasRetorno = new ArrayList<>(metricasImprimirPadrao);
        metricasRetorno.removeAll(this.metricasAH);
        return metricasRetorno;
    }

    private void imprimeCabecalhoMetricas(List<MetricaMetaData> metricas) {
        for (MetricaMetaData metaData : metricas) {
            this.impressor.imprimeCabecalhoColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_METRICA, metaData);
        }
    }

    private void imprimeCabecalhoMetricasTotalizadoresColunas() {
        if (this.cube.getHierarquiaColuna().isEmpty()) {
            for (MetricaMetaData metaData : this.metricasTotalizaSomaColunas) {
                this.impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_TOTAISCOLUNAS, "Acumulado " + metaData.getTitulo());
            }
            for (MetricaMetaData metaData : this.metricasTotalizaMediaColunas) {
                this.impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_TOTAISCOLUNAS, "M�dia " + metaData.getTitulo());
            }
            this.imprimeCabecalhoTotalGeralMetricasColunas();
        } else {
            this.imprimeCabecalhoMetricas(this.metricasTotalizaSomaColunas);
            this.imprimeCabecalhoMetricas(this.metricasTotalizaMediaColunas);
        }

    }

    private void imprimeCabecalhoMetricas(Dimension dimensionColunaPai) {
        if (!this.cube.getHierarquiaColuna().isEmpty()) {
            List<MetricaMetaData> metricasSemAH = this.getMetricasSemAH(this.visibleMetrics);
            for (Dimension dimension : dimensionColunaPai.getDimensionsColumn().values()) {
                if (this.cube.getCuboListener().stopProcess())
                    return;
                if (!dimension.getDimensionsColumn().isEmpty()) {
                    this.imprimeCabecalhoMetricas(dimension);
                } else {
                    List<MetricaMetaData> metricasImprimir = this.visibleMetrics;
                    if (dimension.isFirstDimensionColumnSameLevel()) {
                        metricasImprimir = metricasSemAH;
                    }
                    this.imprimeCabecalhoMetricas(metricasImprimir);
                }
            }
            if (dimensionColunaPai.getMetaData().isTotalizacaoParcial()) {
                List<MetricaMetaData> metricasImprimir = this.visibleMetrics;
                if (dimensionColunaPai.isFirstDimensionColumnSameLevel()) {
                    metricasImprimir = metricasSemAH;
                }
                this.imprimeCabecalhoMetricas(metricasImprimir);
            }
        } else {
            this.imprimeCabecalhoMetricas(this.visibleMetrics);
        }
    }

    private void imprimeCabecalhoDimensoesLinha() {
        for (DimensaoMetaData metaData : this.cube.getHierarquiaLinha()) {
            this.impressor.imprimeCabecalhoDimensaoLinha(metaData);
        }
    }

    private void imprimeLinhaTotalParcialLinhas(Dimension dimensionLinha) {
        this.openLine();
        this.impressor.imprimeCabecalhoTotalParcial(CellProperty.PROPRIEDADE_CELULA_CABECALHO_TOTALPARCIAL,
                "Total", (dimensionLinha.getMetaData().getQtdNiveisAbaixoComSequencia()), 1, dimensionLinha.getMetaData().getFilho());
        List<String> funcoesAlerta = new ArrayList<String>();
        funcoesAlerta.add(MetricaMetaData.TOTALIZACAO_PARCIAL);
        this.imprimeMetricas(dimensionLinha, CellProperty.PROPRIEDADE_CELULA_VALOR_TOTALPARCIALLINHAS,
                new ImpressaoMetricaLinhaTotalizacaoParcialLinhas(this.visibleMetrics), funcoesAlerta, MetricaMetaData.TOTALIZACAO_PARCIAL);
        this.impressor.fechaLinha();
    }

    private void imprimeLinhaMediaParcialLinhas(Dimension dimensionLinha) {
        this.openLine();
        this.impressor.imprimeCabecalhoTotalParcial(CellProperty.PROPRIEDADE_CELULA_CABECALHO_TOTALPARCIAL,
                "M�dia", (dimensionLinha.getMetaData().getQtdNiveisAbaixoComSequencia()), 1, dimensionLinha.getMetaData().getFilho());
        List<String> funcoesAlerta = new ArrayList<>();
        funcoesAlerta.add(MetricaMetaData.MEDIA_PARCIAL);
        this.imprimeMetricas(dimensionLinha, CellProperty.PROPRIEDADE_CELULA_VALOR_TOTALPARCIALLINHAS,
                new ImpressaoMetricaLinhaMediaParcialLinhas(this.visibleMetrics), funcoesAlerta, MetricaMetaData.MEDIA_PARCIAL); // cria o objeto ImpressaoMetricaLinhaTotalizacaoParcialLinhas que vai fazer o calculo
        this.impressor.fechaLinha();
    }

    private void imprimeLinhaExpressaoParcialLinhas(Dimension dimensionLinha) {
        this.openLine();
        this.impressor.imprimeCabecalhoTotalParcial(CellProperty.PROPRIEDADE_CELULA_CABECALHO_TOTALPARCIAL,
                "Express�o", (dimensionLinha.getMetaData().getQtdNiveisAbaixoComSequencia()), 1, dimensionLinha.getMetaData().getFilho());
        List<String> funcoesAlerta = new ArrayList<>();
        funcoesAlerta.add(MetricaMetaData.EXPRESSAO_PARCIAL);
        this.imprimeMetricas(dimensionLinha, CellProperty.PROPRIEDADE_CELULA_VALOR_TOTALPARCIALLINHAS,
                new ImpressaoMetricaLinhaExpressaoParcialLinhas(this.visibleMetrics), funcoesAlerta, MetricaMetaData.EXPRESSAO_PARCIAL); // cria o objeto ImpressaoMetricaLinhaTotalizacaoParcialLinhas que vai fazer o calculo
        this.impressor.fechaLinha();
    }

    private void imprimeCabecalhoTotalizacaoParcialColunas(Dimension dimensionColuna) {
        int qtdMetricas = this.metricsAmount;
        if (dimensionColuna.isFirstDimensionColumnSameLevel()) {
            qtdMetricas = this.getMetricasSemAH(this.visibleMetrics).size();
        }
        this.impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_TOTALPARCIAL, "Total", qtdMetricas, (dimensionColuna.getMetaData().getQtdNiveisAbaixo()));
    }

    private void mergulhaNivelColuna(Iterator<Dimension> it, int nivelImpressao, String propriedadeCelulaNivelAnterior) {
        Dimension dimension = null;
        int i = 0;
        for (i = 0; it.hasNext(); i++) {
            if (i % 100 == 0) {
                if (this.cube.getCuboListener().stopProcess())
                    return;
            }
            dimension = it.next();
            String propriedadeAplicar = propriedadeCelulaNivelAnterior;
            String propriedadeLinhaDimensaoAtual = dimension.searchDimensionAlertLineProperty();
            if (propriedadeLinhaDimensaoAtual != null) {
                propriedadeAplicar = propriedadeLinhaDimensaoAtual;
            }

            if (dimension.getLevel() == nivelImpressao) {
                String propriedadeCelulaDimensaoAtual = dimension.searchDimensionAlertCellProperty();
                if (propriedadeCelulaDimensaoAtual != null) {
                    this.imprimeDimensaoColuna(dimension, propriedadeCelulaDimensaoAtual, i);
                } else {
                    this.imprimeDimensaoColuna(dimension, propriedadeAplicar, i);
                }
            } else if (dimension.getLevel() < nivelImpressao) {
                this.mergulhaNivelColuna(dimension.getDimensionsColumn().values().iterator(), nivelImpressao, propriedadeAplicar);
            }
        }
        if (Objects.requireNonNull(dimension).getLevel() == nivelImpressao && dimension.getParent().getMetaData().isTotalizacaoParcial()) {
            this.imprimeCabecalhoTotalizacaoParcialColunas(dimension.getParent());
        }
    }

    private void mergulhaNivelLinha(Iterator<Dimension> it, String propriedadeCelulaNivelAnterior) {
        Dimension dimension;
        String propriedadeAplicar;
        int i;
        for (i = 0; it.hasNext(); i++) {
            if (i % 100 == 0) {
                if (this.cube.getCuboListener().stopProcess())
                    return;
            }
            dimension = it.next();
            if (i != 0) {
                this.openLine();
            }
            propriedadeAplicar = dimension.getMetaData().getEstiloPadrao();

            if (propriedadeCelulaNivelAnterior != null) {
                propriedadeAplicar = propriedadeCelulaNivelAnterior;
            }

            String propriedadeCelulaNivelAnteriorAplicar = propriedadeCelulaNivelAnterior;
            String propriedadeDimensaoLinhaAtual = dimension.searchDimensionAlertLineProperty();
            if (propriedadeDimensaoLinhaAtual != null) {
                propriedadeAplicar = propriedadeDimensaoLinhaAtual;
                propriedadeCelulaNivelAnteriorAplicar = propriedadeDimensaoLinhaAtual;
            }

            String propriedadeDimensaoCelulaAtual = dimension.searchDimensionAlertCellProperty();
            if (propriedadeDimensaoCelulaAtual != null) {
                propriedadeAplicar = propriedadeDimensaoCelulaAtual;
            }
            this.imprimeDimensaoLinha(dimension, propriedadeAplicar, (i + 1));
            if (!dimension.getDimensionsLine().isEmpty()) {

                Dimensions dims = (Dimensions) dimension.getDimensionsLine().clone();

                Iterator<Dimension> iDimensoes = dims.values().iterator();
                this.mergulhaNivelLinha(iDimensoes, propriedadeCelulaNivelAnteriorAplicar);

                if (dimension.getMetaData().isTotalizacaoParcial() || dimension.getMetaData().isExpressaoParcialTotal()) {
                    this.imprimeLinhaTotalParcialLinhas(dimension);
                }
                if (dimension.getMetaData().isMediaParcial()) {
                    this.imprimeLinhaMediaParcialLinhas(dimension);
                }
                if (dimension.getMetaData().isExpressaoParcial()) {
                    this.imprimeLinhaExpressaoParcialLinhas(dimension);
                }

            } else {
                String propriedadeAlertaCoresMetricaLinhaFuncoes = dimension
                        .searchMetricsPropertyAlertsRowFunctionsTotalColumns(this.visibleMetrics, AlertaCorMetaData.getListaFuncoesTotalizacaoHorizontal());
                if (propriedadeAlertaCoresMetricaLinhaFuncoes != null) {
                    propriedadeCelulaNivelAnteriorAplicar = propriedadeAlertaCoresMetricaLinhaFuncoes;
                    this.alertaMetricaLinhaAtual = propriedadeAlertaCoresMetricaLinhaFuncoes;
                }
                this.imprimeMetricas(dimension, propriedadeCelulaNivelAnteriorAplicar, impressaoMetricaLinha,
                        AlertaCorMetaData.getListaFuncoesTotalizacaoHorizontal(), AlertaCorMetaData.SEM_FUNCAO);
                this.impressor.fechaLinha();
            }
        }
    }

    private void imprimeValorMetricasTotalizacaoParcialColunas(Dimension dimensionLinha, Dimension dimensionColuna,
                                                               ImpressaoMetricaLinha impressao) {
        if (dimensionColuna.getMetaData().isTotalizacaoParcial()) {
            List<MetricaMetaData> metricasImprimirOld = impressao.getMetricas();
            if (dimensionColuna.isFirstDimensionColumnSameLevel()) {
                impressao.setMetricas(this.getMetricasSemAH(metricasImprimirOld));
            }
            String propriedadeCelula = CellProperty.PROPRIEDADE_CELULA_VALOR_TOTALPARCIALLINHAS;
            if (this.alertaMetricaLinhaAtual != null) {
                propriedadeCelula = this.alertaMetricaLinhaAtual;
            }
            impressao.imprimeValoresMetrica(dimensionColuna, null, dimensionLinha, propriedadeCelula,
                    this.impressor, this.cube, CalculoSumarizacaoTipo.TOTAL);
            impressao.setMetricas(metricasImprimirOld);
        }
    }

    private Dimension verificaImprimeTotalizacoesParciaisColuna(Dimension dimensionLinha, Dimension dimensionPaiAnterior,
                                                                Dimension dimensionAtual, ImpressaoMetricaLinha impressao) {
        if (!dimensionAtual.getParent().equals(dimensionPaiAnterior)) {
            this.imprimeValorMetricasTotalizacaoParcialColunas(dimensionLinha, dimensionPaiAnterior, impressao);
            dimensionPaiAnterior = this.verificaImprimeTotalizacoesParciaisColuna(dimensionLinha, dimensionPaiAnterior.getParent(), dimensionAtual.getParent(), impressao);
        }
        return dimensionAtual.getParent();
    }

    private void imprimeMetricas(Dimension dimensionLinha, String propriedadeCelulaNivelAnterior,
                                 ImpressaoMetricaLinha impressaoMetrica, List<String> funcoesAlertaMetricaCelulaConsiderar,
                                 String funcaoAlertaLinhaPesquisar) {

        String propriedadeAplicar = dimensionLinha.getMetricDefaultStyles(this.currentLine);
        if (propriedadeCelulaNivelAnterior != null) {
            propriedadeAplicar = propriedadeCelulaNivelAnterior;
            this.alertaMetricaLinhaAtual = propriedadeCelulaNivelAnterior;
        }
        if (!this.cube.getHierarquiaColuna().isEmpty()) {
            String propriedadeAlertaCoresMetricaLinha = dimensionLinha.searchMetricAlertLineProperty(this.visibleMetrics, funcaoAlertaLinhaPesquisar, this.dimensoesColunaUltimoNivel);
            if (propriedadeAlertaCoresMetricaLinha != null) {
                propriedadeAplicar = propriedadeAlertaCoresMetricaLinha;
                this.alertaMetricaLinhaAtual = propriedadeAlertaCoresMetricaLinha;
            }
            Dimension dimensionPaiAtual = this.dimensoesColunaUltimoNivel.get(0).getParent();
            for (int x = 0; x < this.dimensoesColunaUltimoNivel.size(); x++) {
                if (x % 100 == 0) {
                    if (this.cube.getCuboListener().stopProcess())
                        return;
                }
                Dimension ultimaDimensionColuna = this.dimensoesColunaUltimoNivel.get(x);
                List<MetricaMetaData> metricasImprimirOld = impressaoMetrica.getMetricas();
                List<MetricaMetaData> metricasImprimir = metricasImprimirOld;
                if (x == 0) {
                    metricasImprimir = this.getMetricasSemAH(metricasImprimirOld);
                }
                impressaoMetrica.setMetricas(metricasImprimir);
                dimensionPaiAtual = verificaImprimeTotalizacoesParciaisColuna(dimensionLinha, dimensionPaiAtual, ultimaDimensionColuna, impressaoMetrica);
                impressaoMetrica.imprimeValoresMetrica(dimensionLinha, this.dimensionLinhaAnterior, ultimaDimensionColuna,
                        propriedadeAplicar, this.impressor, cube, impressaoMetrica instanceof ImpressaoMetricaLinhaMediaParcialLinhas ? CalculoSumarizacaoTipo.MEDIA : CalculoSumarizacaoTipo.NORMAL);
                impressaoMetrica.setMetricas(metricasImprimirOld);
            }
            dimensionPaiAtual = this.dimensoesColunaUltimoNivel.get(this.dimensoesColunaUltimoNivel.size() - 1).getParent();
            while (dimensionPaiAtual != null) {
                this.imprimeValorMetricasTotalizacaoParcialColunas(dimensionLinha, dimensionPaiAtual, impressaoMetrica);
                dimensionPaiAtual = dimensionPaiAtual.getParent();
            }
            imprimeValoresTotalizadoresMetricasColunas(dimensionLinha, funcoesAlertaMetricaCelulaConsiderar,
                    impressaoMetrica instanceof ImpressaoMetricaLinhaMediaParcialLinhas ? CalculoSumarizacaoTipo.MEDIA : CalculoSumarizacaoTipo.NORMAL);
            if (!this.metricasTotalizaSomaGeralColunas.isEmpty()) {
                this.imprimeValorTotalGeralMetricasColunas(dimensionLinha, funcoesAlertaMetricaCelulaConsiderar,
                        impressaoMetrica instanceof ImpressaoMetricaLinhaMediaParcialLinhas ? CalculoSumarizacaoTipo.MEDIA : CalculoSumarizacaoTipo.NORMAL);
            }
        } else {
            String propriedadeAlertaCoresMetricaLinha = dimensionLinha.searchMetricAlertLineProperty(
                    this.visibleMetrics, funcaoAlertaLinhaPesquisar, dimensaoColunaNula);
            if (propriedadeAlertaCoresMetricaLinha != null) {
                propriedadeAplicar = propriedadeAlertaCoresMetricaLinha;
                this.alertaMetricaLinhaAtual = propriedadeAlertaCoresMetricaLinha;
            }

            impressaoMetrica.imprimeValoresMetrica(dimensionLinha, this.dimensionLinhaAnterior, dimensaoColunaNula,
                    propriedadeAplicar, this.impressor, cube,
                    impressaoMetrica instanceof ImpressaoMetricaLinhaMediaParcialLinhas ? CalculoSumarizacaoTipo.MEDIA : CalculoSumarizacaoTipo.NORMAL);
            imprimeValoresTotalizadoresMetricasColunas(dimensionLinha, funcoesAlertaMetricaCelulaConsiderar,
                    impressaoMetrica instanceof ImpressaoMetricaLinhaMediaParcialLinhas ? CalculoSumarizacaoTipo.MEDIA : CalculoSumarizacaoTipo.NORMAL);
            if (!this.metricasTotalizaSomaGeralColunas.isEmpty()) {
                this.imprimeValorTotalGeralMetricasColunas(dimensionLinha, funcoesAlertaMetricaCelulaConsiderar,
                        impressaoMetrica instanceof ImpressaoMetricaLinhaMediaParcialLinhas ? CalculoSumarizacaoTipo.MEDIA : CalculoSumarizacaoTipo.NORMAL);
            }
        }
        if ("semFuncao".equalsIgnoreCase(funcaoAlertaLinhaPesquisar)) {
            this.dimensionLinhaAnterior = dimensionLinha;
        }
    }

    private void imprimeValoresTotalizadoresMetricasColunas(Dimension dimensionLinha, List<String> funcoesAlertaAtuais, String tipoLinha) {
        String propriedadeCelula = CellProperty.PROPRIEDADE_CELULA_TOTALGERAL;
        if (this.alertaMetricaLinhaAtual != null) {
            propriedadeCelula = this.alertaMetricaLinhaAtual;
        }

        ImpressaoMetricaLinhaTotalizacaoColunas impressaoSoma = new ImpressaoMetricaLinhaTotalizacaoColunas(
                this.metricasTotalizaSomaColunas, CalculoSumarizacaoTipoSomatorio.getInstance(), MetricaMetaData.VALOR_ACUMULADO_AH, funcoesAlertaAtuais);
        impressaoSoma.imprimeValoresMetrica(dimensionLinha, null, dimensaoColunaNula, propriedadeCelula, this.impressor, cube, tipoLinha);

        ImpressaoMetricaLinhaTotalizacaoColunas impressaoMedia = new ImpressaoMetricaLinhaTotalizacaoColunas(
                this.metricasTotalizaMediaColunas, CalculoSumarizacaoTipoMediaColuna.getInstance(), MetricaMetaData.MEDIA_AH, funcoesAlertaAtuais);
        impressaoMedia.imprimeValoresMetrica(dimensionLinha, null, dimensaoColunaNula, propriedadeCelula, this.impressor, cube, tipoLinha);
    }

    private void imprimeValorTotalGeralMetricasColunas(Dimension dimensionLinha, List<String> funcoesAlertaAtuais, String tipoLinha) {
        ImpressaoMetricaLinhaTotalizacaoColunasGeral impressao = new ImpressaoMetricaLinhaTotalizacaoColunasGeral(this.metricasTotalizaSomaGeralColunas, funcoesAlertaAtuais);
        String propriedadeCelula = CellProperty.PROPRIEDADE_CELULA_TOTALGERAL;
        if (this.alertaMetricaLinhaAtual != null) {
            propriedadeCelula = this.alertaMetricaLinhaAtual;
        }
        impressao.imprimeValoresMetrica(dimensionLinha, null, dimensaoColunaNula, propriedadeCelula, impressor, cube, tipoLinha);
    }

    private int getQtdMetricasTotaisParciaisAbaixoColuna(Dimension dimension, int qtdMetricasTotal, int qtdMetricasSemAH) {
        int retorno = 0;
        if (!dimension.getMetaData().isUltima()) {
            if (dimension.getMetaData().isTotalizacaoParcial()) {
                if (!dimension.isFirstDimensionColumnSameLevel()) {
                    retorno += qtdMetricasTotal;
                } else {
                    retorno += qtdMetricasSemAH;
                }
            }
            for (Dimension dimensionFilha : dimension.getDimensoesAbaixo().values()) {
                retorno += getQtdMetricasTotaisParciaisAbaixoColuna(dimensionFilha, qtdMetricasTotal, qtdMetricasSemAH);
            }
        }
        return retorno;
    }

    public void imprimeDimensaoColuna(Dimension dim, String propriedadeCelulaNivelAnterior, int indiceDimensao) {
        int qtdMetricasRemover = 0;
        boolean isPrimeiraDimensao = dim.isFirstDimensionColumnSameLevel();
        if (isPrimeiraDimensao) {
            qtdMetricasRemover = this.metricasAH.size();
        }
        int colspan = (this.metricsAmount * dim.getTotalSize()) - qtdMetricasRemover;
        colspan += this.getQtdMetricasTotaisParciaisAbaixoColuna(dim, this.metricsAmount, this.metricsAmount - qtdMetricasRemover);
        this.impressor.imprimeValorColuna(propriedadeCelulaNivelAnterior, colspan, 1, dim.getVisualizationValue(), dim.getMetaData());
    }

    public void imprimeDimensaoLinha(Dimension dim, String propriedadeCelulaNivelAnterior, int sequencia) {
        DimensionLinha dimensaoLinha = (DimensionLinha) dim;
        int rowspan = dimensaoLinha.getTotalSize() + dimensaoLinha.countPartialAggregatesInHierarchy();
        int colspan = dimensaoLinha.getColspanImpressaoLinha();
        String sequenciaRanking = Optional.ofNullable(dimensaoLinha.getRankingSequence()).map(String::valueOf).orElse(this.impressor.getValorVazio());
        if (dim.getMetaData().hasCampoSequencia()) {
            this.impressor.imprimeCampoSequencia(dim.getMetaData(), sequenciaRanking, 1, rowspan);
        }
        this.impressor.imprimeValorDimensaoLinha(propriedadeCelulaNivelAnterior, colspan, rowspan, dimensaoLinha.getVisualizationValue(), dimensaoLinha.getMetaData());
    }
}