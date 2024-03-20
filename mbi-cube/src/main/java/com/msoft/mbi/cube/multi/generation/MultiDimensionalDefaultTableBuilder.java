package com.msoft.mbi.cube.multi.generation;

import java.util.*;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditions;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionNullColumn;
import com.msoft.mbi.cube.multi.dimension.DimensionLine;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimensions;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedEvolucaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaFuncaoMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class MultiDimensionalDefaultTableBuilder extends tableGenerator {
    private final List<MetricMetaData> metricasTotalizaSomaColunas;
    private List<MetricMetaData> metricasTotalizaSomaGeralColunas;
    private final List<MetricMetaData> metricasTotalizaMediaColunas;
    private final List<MetricMetaData> metricasAH;
    private ImpressaoMetricaLinhaAtual impressaoMetricaLinha = null;
    private DimensionNullColumn dimensaoColunaNula = null;
    private String alertaMetricaLinhaAtual;
    private List<Dimension> dimensoesColunaUltimoNivel = null;

    private Dimension dimensionLinhaAnterior = null;

    public MultiDimensionalDefaultTableBuilder(Cube cube) {
        this.cube = cube;
        dimensaoColunaNula = new DimensionNullColumn(cube);
        this.metricsAmount = 0;
        this.visibleMetrics = new ArrayList<>();
        this.metricasTotalizaSomaColunas = new ArrayList<>();
        this.metricasTotalizaSomaGeralColunas = new ArrayList<>();
        this.metricasTotalizaMediaColunas = new ArrayList<>();
        this.metricasAH = new ArrayList<>();
        this.dimensoesColunaUltimoNivel = this.cube.getDimensionsLastLevelColumns();
        this.populaMetricasVisualizadas();
        this.impressaoMetricaLinha = new ImpressaoMetricaLinhaAtual(this.visibleMetrics);
    }

    private void processarTabelaSemDados() {
        this.printer.startPrinting();
        this.openLine();
        this.printer.printColumn(CellProperty.CELL_PROPERTY_DIMENSION_HEADER, "A pesquisa não retornou dados");
        this.printer.closeLine();
        this.printer.endPrinting();
    }

    public void processar(Printer iPrinter) {
        this.printer = iPrinter;
        if (!cube.getDimensionsLine().isEmpty()) {
            this.createDefaultStyles();
            this.printer.startPrinting();
            this.printer.openHeadLine();
            int nivelImpressao = 0;
            int colspanNiveisLinhaComSequencia = this.cube.getMetaData().getQtdNiveisAbaixoComSequencia();
            int expansaoMaximaColuna = this.cube.getHierarchyColumn().size();
            int verificaProcessamento = 0;
            for (DimensaoMetaData metadata : this.cube.getHierarchyColumn()) {
                verificaProcessamento++;
                if (verificaProcessamento % 100 == 0) {
                    if (this.cube.getCubeListener().stopProcess())
                        return;
                }
                this.openLine();
                this.printer.printColumnHeader(CellProperty.CELL_PROPERTY_DIMENSION_HEADER, metadata, colspanNiveisLinhaComSequencia, 1);

                if (!this.cube.getDimensionsColumn().isEmpty()) {
                    Collection<Dimension> set = this.cube.getDimensionsColumn().values();
                    Iterator<Dimension> it = set.iterator();
                    this.mergulhaNivelColuna(it, nivelImpressao, CellProperty.CELL_PROPERTY_DIMENSION_VALUE);
                }

                nivelImpressao++;
                if (nivelImpressao == 1) {
                    this.imprimeCabecalhoTotalizadoresColunas();
                    this.imprimeCabecalhoTotalGeralMetricasColunas();
                } else if (nivelImpressao == expansaoMaximaColuna) {
                    break;
                }
                this.printer.closeLine();
            }

            this.openLine();
            this.imprimeCabecalhoDimensoesLinha();

            this.imprimeCabecalhoMetricas(this.cube);
            this.imprimeCabecalhoMetricasTotalizadoresColunas();

            this.printer.closeLine();
            this.printer.closeHeadLine();

            this.printer.openBodyLine();
            if (!this.cube.getDimensionsLine().isEmpty()) {
                Collection<Dimension> set = ((Dimensions) this.cube.getDimensionsLine().clone()).values();
                Iterator<Dimension> it = set.iterator();
                this.openLine();
                this.mergulhaNivelLinha(it, null);
                nivelImpressao = 0;
                this.imprimeLinhaTotalGeralLinhas(colspanNiveisLinhaComSequencia);
            }
            nivelImpressao++;
            this.printer.closeBodyLine();
            this.printer.endPrinting();

        } else {
            this.printer.setDefaultBorderColor("3377CC");
            criaEstiloCabecalhoDimensao();
            this.processarTabelaSemDados();
        }
    }

    private void criaEstilosAlertasDeCoresComLink(List<?> alertasCores) {
        for (Object oAlertaCores : alertasCores) {
            ColorAlertConditions alertaCores = (ColorAlertConditions) oAlertaCores;
            this.printer.addStyle(alertaCores.getAlertProperty(), CellProperty.CELL_PROPERTY_ALERTS_PREFIX + alertaCores.getSequence());
            CellProperty cellProperty = new CellProperty();
            cellProperty.addExtraAttributes("cursor", "pointer");
            this.printer.addLinkStyle(cellProperty, CellProperty.CELL_PROPERTY_ALERTS_PREFIX + alertaCores.getSequence() + " span");
        }
    }

    protected void openLine() {
        super.openLine();
        this.alertaMetricaLinhaAtual = null;
    }

    private void criaEstilosAlertasDeCores() {
        for (MetricMetaData metaData : this.cube.getHierarchyMetric()) {
            this.createColorAlertStyles(metaData.getColorAlertCells());
            this.createColorAlertStyles(metaData.getColorsAlertLines());
        }
        for (DimensaoMetaData metaData : this.cube.getHierarchyColumn()) {
            this.createColorAlertStyles(metaData.getColorAlertCells());
            this.createColorAlertStyles(metaData.getAlertasCoresLinha());
        }
        for (DimensaoMetaData metaData : this.cube.getHierarchyLine()) {
            this.criaEstilosAlertasDeCoresComLink(metaData.getColorAlertCells());
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

        this.printer.addColumnHeaderStyle(cellProperty, CellProperty.CELL_PROPERTY_DIMENSION_HEADER);
    }

    protected void createDefaultStyles() {
        this.printer.setDefaultBorderColor("3377CC");
        super.createDefaultStyles();
        CellProperty propriedadeDimensaoPadrao = new CellProperty();
        propriedadeDimensaoPadrao.setAlignment(CellProperty.ALIGNMENT_LEFT);
        propriedadeDimensaoPadrao.setFontColor("000080");
        propriedadeDimensaoPadrao.setBackGroundColor("A2C8E8");
        propriedadeDimensaoPadrao.setFontName("Verdana");
        propriedadeDimensaoPadrao.setFontSize(10);
        propriedadeDimensaoPadrao.setSpecificBorder(true);
        propriedadeDimensaoPadrao.setBorderColor("3377CC");
        propriedadeDimensaoPadrao.addExtraAttributes("border-top", "none");
        propriedadeDimensaoPadrao.addExtraAttributes("border-left", "none");
        this.printer.addStyle(propriedadeDimensaoPadrao, CellProperty.CELL_PROPERTY_DIMENSION_VALUE);

        CellProperty cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.printer.addLinkStyle(cellProperty, CellProperty.CELL_PROPERTY_DIMENSION_VALUE + " span");

        cellProperty = new CellProperty();
        cellProperty.setAlignment(CellProperty.ALIGNMENT_RIGHT);
        cellProperty.setFontColor("000080");
        cellProperty.setBackGroundColor("CCCCCC");
        cellProperty.setFontName("Verdana");
        cellProperty.setFontSize(10);
        cellProperty.setBold(true);
        //propriedadeCelula.addOutroAtributo("white-space", "nowrap");
        cellProperty.setBorderColor("3377CC");
        cellProperty.setSpecificBorder(true);
        cellProperty.addExtraAttributes("border-top", "none");
        this.printer.addStyle(cellProperty, CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_LINES);

        cellProperty = new CellProperty();
        cellProperty.setAlignment(CellProperty.ALIGNMENT_LEFT);
        cellProperty.setFontColor("000080");
        cellProperty.setBackGroundColor("CCCCCC");
        cellProperty.setFontName("Verdana");
        cellProperty.setFontSize(10);
        cellProperty.setBold(true);
        cellProperty.setBorderColor("3377CC");
        cellProperty.setSpecificBorder(true);
        cellProperty.addExtraAttributes("border-top", "none");
        this.printer.addStyle(cellProperty, CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_HEADER);

        cellProperty = new CellProperty();
        cellProperty.setAlignment(CellProperty.ALIGNMENT_CENTER);
        cellProperty.setFontColor("000080");
        cellProperty.setBackGroundColor("CCCCCC");
        cellProperty.setFontName("Verdana");
        cellProperty.setFontSize(10);
        cellProperty.setBold(true);
        cellProperty.setBorderColor("3377CC");
        cellProperty.setSpecificBorder(true);
        cellProperty.addExtraAttributes("border-top", "none");
        this.printer.addStyle(cellProperty, CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER);

        criaEstiloCabecalhoDimensao();

        cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.printer.addLinkStyle(cellProperty, CellProperty.CELL_PROPERTY_DIMENSION_HEADER + " span");

        cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.printer.addLinkStyle(cellProperty, CellProperty.CELL_PROPERTY_DIMENSION_HEADER + " IMG");

        CellProperty propriedadeCabecalhoMetrica = new CellProperty();
        propriedadeCabecalhoMetrica.setAlignment(CellProperty.ALIGNMENT_CENTER);
        propriedadeCabecalhoMetrica.setFontColor("000080");
        propriedadeCabecalhoMetrica.setBackGroundColor("A2C8E8");
        propriedadeCabecalhoMetrica.setFontName("Verdana");
        propriedadeCabecalhoMetrica.setBold(true);
        propriedadeCabecalhoMetrica.setFontSize(10);
        propriedadeCabecalhoMetrica.setSpecificBorder(true);
        propriedadeCabecalhoMetrica.setBorderColor("3377CC");
        this.printer.addColumnHeaderStyle(propriedadeCabecalhoMetrica, CellProperty.CELL_PROPERTY_METRIC_HEADER);

        cellProperty = new CellProperty();
        cellProperty.addExtraAttributes("cursor", "pointer");
        this.printer.addLinkStyle(cellProperty, CellProperty.CELL_PROPERTY_METRIC_HEADER + " span");

        CellProperty cellPropertyValorMetricaOutros = new CellProperty();
        cellPropertyValorMetricaOutros.setAlignment(CellProperty.ALIGNMENT_LEFT);
        cellPropertyValorMetricaOutros.setFontColor("000080");
        cellPropertyValorMetricaOutros.setBackGroundColor("A2C8E8");
        cellPropertyValorMetricaOutros.setFontName("Verdana");
        cellPropertyValorMetricaOutros.setFontSize(10);
        //propriedadeCelulaValorMetricaOutros.addOutroAtributo("white-space", "nowrap");
        cellPropertyValorMetricaOutros.addExtraAttributes("border-left", "none");
        cellPropertyValorMetricaOutros.addExtraAttributes("border-right", "1px solid #3377CC");
        cellPropertyValorMetricaOutros.setBorderColor("3377CC");
        cellPropertyValorMetricaOutros.setSpecificBorder(true);
        this.printer.addStyle(cellPropertyValorMetricaOutros, CellProperty.CELL_PROPERTY_OTHERS);

        this.criaEstilosAlertasDeCores();

        super.createsSpecificStylesColumns();
    }

    private void populaMetricasVisualizadas() {
        boolean temDimensaoColuna = !this.cube.getHierarchyColumn().isEmpty();
        this.metricasTotalizaSomaGeralColunas = this.cube.getMetricsTotalHorizontal();
        int verificaProcessamento = 0;
        for (MetricMetaData metaData : this.cube.getHierarchyMetric()) {
            verificaProcessamento++;
            if (verificaProcessamento % 100 == 0) {
                if (this.cube.getCubeListener().stopProcess())
                    return;
            }
            if (metaData.isViewed()) {
                if (metaData.isTotalSumColumns()) {
                    this.metricasTotalizaSomaColunas.add(metaData);
                }
                if (metaData.isTotalMediaColumns()) {
                    this.metricasTotalizaMediaColunas.add(metaData);
                }

                if (metaData instanceof MetricaCalculadaFuncaoMetaData metricaDeAnalise) {
                    if (metricaDeAnalise.getFuncaoCampo().equals(MetricCalculatedEvolucaoAHMetaData.AH)) {
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
            this.printer.printColumn(CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER, "Acumulado", this.metricasTotalizaSomaColunas.size(), this.cube.getHierarchyColumn().size());
        }
        if (!this.metricasTotalizaMediaColunas.isEmpty()) {
            this.printer.printColumn(CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER, "M�dia", this.metricasTotalizaMediaColunas.size(), this.cube.getHierarchyColumn().size());
        }
    }

    private void imprimeCabecalhoTotalGeralMetricasColunas() {
        if (!this.metricasTotalizaSomaGeralColunas.isEmpty()) {
            this.printer.printColumn(CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER, "Total", 1, this.cube.getHierarchyColumn().size() + 1);
        }
    }

    private void imprimeLinhaTotalGeralLinhas(int colspanNiveisLinhaComSequencia) {
        this.openLine();
        this.printer.printColumn(CellProperty.CELL_PROPERTY_TOTAL_GENERAL, this.printer.getEmptyValue(), colspanNiveisLinhaComSequencia, 1);
        List<String> funcoesAlerta = new ArrayList<>();
        funcoesAlerta.add(MetricMetaData.TOTAL_GENERAL);

        String propriedadeTotalGeral = CellProperty.CELL_PROPERTY_TOTAL_GENERAL;
        String propriedadeAlertaCoresMetricaLinhaFuncoes = this.cube.searchMetricsPropertyAlertsRowFunctionsTotalColumns(this.visibleMetrics, funcoesAlerta);
        if (propriedadeAlertaCoresMetricaLinhaFuncoes != null) {
            propriedadeTotalGeral = propriedadeAlertaCoresMetricaLinhaFuncoes;
            this.alertaMetricaLinhaAtual = propriedadeAlertaCoresMetricaLinhaFuncoes;
        }
        funcoesAlerta.add(MetricMetaData.TOTAL_AV);
        this.imprimeMetricas(this.cube, propriedadeTotalGeral, new ImpressaoMetricaLinhaTotalizacaoLinhas(this.visibleMetrics, funcoesAlerta), funcoesAlerta, MetricMetaData.TOTAL_AV);
        this.printer.closeLine();
    }

    private List<MetricMetaData> getMetricasSemAH(List<MetricMetaData> metricasImprimirPadrao) {
        List<MetricMetaData> metricasRetorno = new ArrayList<>(metricasImprimirPadrao);
        metricasRetorno.removeAll(this.metricasAH);
        return metricasRetorno;
    }

    private void imprimeCabecalhoMetricas(List<MetricMetaData> metricas) {
        for (MetricMetaData metaData : metricas) {
            this.printer.printColumnHeader(CellProperty.CELL_PROPERTY_METRIC_HEADER, metaData);
        }
    }

    private void imprimeCabecalhoMetricasTotalizadoresColunas() {
        if (this.cube.getHierarchyColumn().isEmpty()) {
            for (MetricMetaData metaData : this.metricasTotalizaSomaColunas) {
                this.printer.printColumn(CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER, "Acumulado " + metaData.getTitle());
            }
            for (MetricMetaData metaData : this.metricasTotalizaMediaColunas) {
                this.printer.printColumn(CellProperty.CELL_PROPERTY_COLUMN_TOTAL_HEADER, "M�dia " + metaData.getTitle());
            }
            this.imprimeCabecalhoTotalGeralMetricasColunas();
        } else {
            this.imprimeCabecalhoMetricas(this.metricasTotalizaSomaColunas);
            this.imprimeCabecalhoMetricas(this.metricasTotalizaMediaColunas);
        }

    }

    private void imprimeCabecalhoMetricas(Dimension dimensionColunaPai) {
        if (!this.cube.getHierarchyColumn().isEmpty()) {
            List<MetricMetaData> metricasSemAH = this.getMetricasSemAH(this.visibleMetrics);
            for (Dimension dimension : dimensionColunaPai.getDimensionsColumn().values()) {
                if (this.cube.getCubeListener().stopProcess())
                    return;
                if (!dimension.getDimensionsColumn().isEmpty()) {
                    this.imprimeCabecalhoMetricas(dimension);
                } else {
                    List<MetricMetaData> metricasImprimir = this.visibleMetrics;
                    if (dimension.isFirstDimensionColumnSameLevel()) {
                        metricasImprimir = metricasSemAH;
                    }
                    this.imprimeCabecalhoMetricas(metricasImprimir);
                }
            }
            if (dimensionColunaPai.getMetaData().isTotalizacaoParcial()) {
                List<MetricMetaData> metricasImprimir = this.visibleMetrics;
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
        for (DimensaoMetaData metaData : this.cube.getHierarchyLine()) {
            this.printer.printDimensionLineHeader(metaData);
        }
    }

    private void imprimeLinhaTotalParcialLinhas(Dimension dimensionLinha) {
        this.openLine();
        this.printer.printTotalPartialHeader(CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_HEADER,
                "Total", (dimensionLinha.getMetaData().getQtdNiveisAbaixoComSequencia()), 1, dimensionLinha.getMetaData().getFilho());
        List<String> funcoesAlerta = new ArrayList<String>();
        funcoesAlerta.add(MetricMetaData.TOTAL_PARTIAL);
        this.imprimeMetricas(dimensionLinha, CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_LINES,
                new ImpressaoMetricaLinhaTotalizacaoParcialLinhas(this.visibleMetrics), funcoesAlerta, MetricMetaData.TOTAL_PARTIAL);
        this.printer.closeLine();
    }

    private void imprimeLinhaMediaParcialLinhas(Dimension dimensionLinha) {
        this.openLine();
        this.printer.printTotalPartialHeader(CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_HEADER,
                "M�dia", (dimensionLinha.getMetaData().getQtdNiveisAbaixoComSequencia()), 1, dimensionLinha.getMetaData().getFilho());
        List<String> funcoesAlerta = new ArrayList<>();
        funcoesAlerta.add(MetricMetaData.MEDIA_PARTIAL);
        this.imprimeMetricas(dimensionLinha, CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_LINES,
                new ImpressaoMetricaLinhaMediaParcialLinhas(this.visibleMetrics), funcoesAlerta, MetricMetaData.MEDIA_PARTIAL); // cria o objeto ImpressaoMetricaLinhaTotalizacaoParcialLinhas que vai fazer o calculo
        this.printer.closeLine();
    }

    private void imprimeLinhaExpressaoParcialLinhas(Dimension dimensionLinha) {
        this.openLine();
        this.printer.printTotalPartialHeader(CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_HEADER,
                "Express�o", (dimensionLinha.getMetaData().getQtdNiveisAbaixoComSequencia()), 1, dimensionLinha.getMetaData().getFilho());
        List<String> funcoesAlerta = new ArrayList<>();
        funcoesAlerta.add(MetricMetaData.EXPRESSION_PARTIAL);
        this.imprimeMetricas(dimensionLinha, CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_LINES,
                new ImpressaoMetricaLinhaExpressaoParcialLinhas(this.visibleMetrics), funcoesAlerta, MetricMetaData.EXPRESSION_PARTIAL); // cria o objeto ImpressaoMetricaLinhaTotalizacaoParcialLinhas que vai fazer o calculo
        this.printer.closeLine();
    }

    private void imprimeCabecalhoTotalizacaoParcialColunas(Dimension dimensionColuna) {
        int qtdMetricas = this.metricsAmount;
        if (dimensionColuna.isFirstDimensionColumnSameLevel()) {
            qtdMetricas = this.getMetricasSemAH(this.visibleMetrics).size();
        }
        this.printer.printColumn(CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_HEADER, "Total", qtdMetricas, (dimensionColuna.getMetaData().getQtdNiveisAbaixo()));
    }

    private void mergulhaNivelColuna(Iterator<Dimension> it, int nivelImpressao, String propriedadeCelulaNivelAnterior) {
        Dimension dimension = null;
        int i = 0;
        for (i = 0; it.hasNext(); i++) {
            if (i % 100 == 0) {
                if (this.cube.getCubeListener().stopProcess())
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
                if (this.cube.getCubeListener().stopProcess())
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

                if (dimension.getMetaData().isTotalizacaoParcial() || dimension.getMetaData().isPartialTotalExpression()) {
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
                        .searchMetricsPropertyAlertsRowFunctionsTotalColumns(this.visibleMetrics, ColorAlertMetadata.getHorizaontalToalFunctionList());
                if (propriedadeAlertaCoresMetricaLinhaFuncoes != null) {
                    propriedadeCelulaNivelAnteriorAplicar = propriedadeAlertaCoresMetricaLinhaFuncoes;
                    this.alertaMetricaLinhaAtual = propriedadeAlertaCoresMetricaLinhaFuncoes;
                }
                this.imprimeMetricas(dimension, propriedadeCelulaNivelAnteriorAplicar, impressaoMetricaLinha,
                        ColorAlertMetadata.getHorizaontalToalFunctionList(), ColorAlertMetadata.NO_FUNCTION);
                this.printer.closeLine();
            }
        }
    }

    private void imprimeValorMetricasTotalizacaoParcialColunas(Dimension dimensionLinha, Dimension dimensionColuna,
                                                               ImpressaoMetricaLinha impressao) {
        if (dimensionColuna.getMetaData().isTotalizacaoParcial()) {
            List<MetricMetaData> metricasImprimirOld = impressao.getMetricas();
            if (dimensionColuna.isFirstDimensionColumnSameLevel()) {
                impressao.setMetricas(this.getMetricasSemAH(metricasImprimirOld));
            }
            String propriedadeCelula = CellProperty.CELL_PROPERTY_TOTAL_PARTIAL_LINES;
            if (this.alertaMetricaLinhaAtual != null) {
                propriedadeCelula = this.alertaMetricaLinhaAtual;
            }
            impressao.imprimeValoresMetrica(dimensionColuna, null, dimensionLinha, propriedadeCelula,
                    this.printer, this.cube, CalculoSumarizacaoTipo.TOTAL);
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
        if (!this.cube.getHierarchyColumn().isEmpty()) {
            String propriedadeAlertaCoresMetricaLinha = dimensionLinha.searchMetricAlertLineProperty(this.visibleMetrics, funcaoAlertaLinhaPesquisar, this.dimensoesColunaUltimoNivel);
            if (propriedadeAlertaCoresMetricaLinha != null) {
                propriedadeAplicar = propriedadeAlertaCoresMetricaLinha;
                this.alertaMetricaLinhaAtual = propriedadeAlertaCoresMetricaLinha;
            }
            Dimension dimensionPaiAtual = this.dimensoesColunaUltimoNivel.get(0).getParent();
            for (int x = 0; x < this.dimensoesColunaUltimoNivel.size(); x++) {
                if (x % 100 == 0) {
                    if (this.cube.getCubeListener().stopProcess())
                        return;
                }
                Dimension ultimaDimensionColuna = this.dimensoesColunaUltimoNivel.get(x);
                List<MetricMetaData> metricasImprimirOld = impressaoMetrica.getMetricas();
                List<MetricMetaData> metricasImprimir = metricasImprimirOld;
                if (x == 0) {
                    metricasImprimir = this.getMetricasSemAH(metricasImprimirOld);
                }
                impressaoMetrica.setMetricas(metricasImprimir);
                dimensionPaiAtual = verificaImprimeTotalizacoesParciaisColuna(dimensionLinha, dimensionPaiAtual, ultimaDimensionColuna, impressaoMetrica);
                impressaoMetrica.imprimeValoresMetrica(dimensionLinha, this.dimensionLinhaAnterior, ultimaDimensionColuna,
                        propriedadeAplicar, this.printer, cube, impressaoMetrica instanceof ImpressaoMetricaLinhaMediaParcialLinhas ? CalculoSumarizacaoTipo.MEDIA : CalculoSumarizacaoTipo.NORMAL);
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
                    propriedadeAplicar, this.printer, cube,
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
        String propriedadeCelula = CellProperty.CELL_PROPERTY_TOTAL_GENERAL;
        if (this.alertaMetricaLinhaAtual != null) {
            propriedadeCelula = this.alertaMetricaLinhaAtual;
        }

        ImpressaoMetricaLinhaTotalizacaoColunas impressaoSoma = new ImpressaoMetricaLinhaTotalizacaoColunas(
                this.metricasTotalizaSomaColunas, CalculoSumarizacaoTipoSomatorio.getInstance(), MetricMetaData.ACCUMULATED_VALUE_AH, funcoesAlertaAtuais);
        impressaoSoma.imprimeValoresMetrica(dimensionLinha, null, dimensaoColunaNula, propriedadeCelula, this.printer, cube, tipoLinha);

        ImpressaoMetricaLinhaTotalizacaoColunas impressaoMedia = new ImpressaoMetricaLinhaTotalizacaoColunas(
                this.metricasTotalizaMediaColunas, CalculoSumarizacaoTipoMediaColuna.getInstance(), MetricMetaData.MEDIA_AH, funcoesAlertaAtuais);
        impressaoMedia.imprimeValoresMetrica(dimensionLinha, null, dimensaoColunaNula, propriedadeCelula, this.printer, cube, tipoLinha);
    }

    private void imprimeValorTotalGeralMetricasColunas(Dimension dimensionLinha, List<String> funcoesAlertaAtuais, String tipoLinha) {
        ImpressaoMetricaLinhaTotalizacaoColunasGeral impressao = new ImpressaoMetricaLinhaTotalizacaoColunasGeral(this.metricasTotalizaSomaGeralColunas, funcoesAlertaAtuais);
        String propriedadeCelula = CellProperty.CELL_PROPERTY_TOTAL_GENERAL;
        if (this.alertaMetricaLinhaAtual != null) {
            propriedadeCelula = this.alertaMetricaLinhaAtual;
        }
        impressao.imprimeValoresMetrica(dimensionLinha, null, dimensaoColunaNula, propriedadeCelula, printer, cube, tipoLinha);
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
            for (Dimension dimensionFilha : dimension.getDimensionsBelow().values()) {
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
        this.printer.printColumnValue(propriedadeCelulaNivelAnterior, colspan, 1, dim.getVisualizationValue(), dim.getMetaData());
    }

    public void imprimeDimensaoLinha(Dimension dim, String propriedadeCelulaNivelAnterior, int sequencia) {
        DimensionLine dimensaoLinha = (DimensionLine) dim;
        int rowspan = dimensaoLinha.getTotalSize() + dimensaoLinha.countPartialAggregatesInHierarchy();
        int colspan = dimensaoLinha.getColspanImpressaoLinha();
        String sequenciaRanking = Optional.ofNullable(dimensaoLinha.getRankingSequence()).map(String::valueOf).orElse(this.printer.getEmptyValue());
        if (dim.getMetaData().hasSequenceFields()) {
            this.printer.printSequenceField(dim.getMetaData(), sequenciaRanking, 1, rowspan);
        }
        this.printer.printDimensionLineValue(propriedadeCelulaNivelAnterior, colspan, rowspan, dimensaoLinha.getVisualizationValue(), dimensaoLinha.getMetaData());
    }
}