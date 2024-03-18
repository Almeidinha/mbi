package com.msoft.mbi.cube.multi.generation;

import java.util.*;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MetricsMap;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.column.TipoData;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditions;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionColunaNula;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.metrics.Metric;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class DefaultTableBuilder extends tableGenerator {

    private Map<String, Object> currentLineValues = new HashMap<>();
    private final Map<Integer, Map<String, String>> currentLineCellProperties = new HashMap<>();
    private boolean hasSequence = false;
    private MetricLine previousMetricLine = null;

    public DefaultTableBuilder(Cube cube) {
        this.cube = cube;
        this.metricsAmount = 0;
        this.visibleMetrics = new ArrayList<>();
        this.populaMetricasVisualizadas();
        this.populaMapPropriedades();
        this.hasSequence = this.cube.getColumnsViewed().get(0).hasSequenceFields();
    }

    public void processar(Impressor iImpressor) {
        this.impressor = iImpressor;
        this.createDefaultStyles();
        this.impressor.iniciaImpressao();

        this.impressor.abreLinhaHead();
        this.openLine();
        this.imprimeCabecalho();
        this.impressor.fechaLinha();
        this.impressor.fechaLinhaHead();

        this.impressor.abreLinhaBody();
        List<Dimension> dimensoes = this.cube.getDimensionsLastLevelLines();
        if (!dimensoes.isEmpty()) {
            for (Dimension dimensionLinha : dimensoes) {
                this.mergulhaNivelLinha(dimensionLinha);
            }
            this.imprimeLinhaTotalGeralLinhas();
        }
        this.impressor.fechaLinhaBody();
        this.impressor.finalizaImpressao();

        this.populaMapPropriedades();
    }

    private void imprimeLinhaTotalGeralLinhas() {
        this.currentLineValues = new HashMap<>();
        DimensionColunaNula dimensaoColunaNula = new DimensionColunaNula(this.cube);
        for (MetricMetaData metricMetaData : this.visibleMetrics) {
            String titulo = metricMetaData.getTitle();
            if (metricMetaData.isTotalLines()) {
                Double valor = metricMetaData.calculaValorTotalParcial(this.cube, dimensaoColunaNula);
                this.currentLineValues.put(titulo, valor);
            }
        }
        this.openLine();
        if (hasSequence) {
            impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_TOTALGERAL, this.impressor.getValorVazio());
        }

        for (ColumnMetaData coluna : this.cube.getColumnsViewed()) {
            if (this.currentLineValues.containsKey(coluna.getTitle())) {
                coluna.printFieldTypeValue(this.currentLineValues.get(coluna.getTitle()), CellProperty.PROPRIEDADE_CELULA_TOTALGERAL, impressor);
            } else {
                this.impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_TOTALGERAL, this.impressor.getValorVazio());
            }
        }
        this.impressor.fechaLinha();
    }

    private void criaEstilosAlertasDeCores() {
        for (MetricMetaData metaData : this.cube.getHierarchyMetric()) {
            this.createColorAlertStyles(metaData.getColorAlertCells());
        }
    }

    protected void createDefaultStyles() {
        this.impressor.setCorBordasPadrao("3377CC");
        super.createDefaultStyles();

        CellProperty cellProperty = CellProperty.builder()
                .alignment(CellProperty.ALINHAMENTO_CENTRO)
                .fontColor("FFFFFF")
                .backGroundColor("3377CC")
                .fontName("Verdana")
                .fontSize(10)
                .extraAttributes(Map.of("border", "1px solid #FFFFFF"))
                .build();

        this.impressor.adicionaEstiloCabecalhoColuna(cellProperty, CellProperty.PROPRIEDADE_CELULA_CABECALHO_PADRAO);
        this.impressor.adicionaEstilo(cellProperty, CellProperty.PROPRIEDADE_CELULA_CABECALHO_SEQUENCIA);

        CellProperty headerProperties = CellProperty.builder().extraAttributes(Map.of("cursor", "pointer")).build();

        this.impressor.adicionaEstiloLink(headerProperties, CellProperty.PROPRIEDADE_CELULA_CABECALHO_PADRAO + " span");
        this.impressor.adicionaEstiloLink(headerProperties, CellProperty.PROPRIEDADE_CELULA_CABECALHO_PADRAO + " IMG");
        this.impressor.adicionaEstiloLink(headerProperties, CellProperty.PROPRIEDADE_CELULA_VALOR_METRICA1 + " span");
        this.impressor.adicionaEstiloLink(headerProperties, CellProperty.PROPRIEDADE_CELULA_VALOR_METRICA2 + " span");

        createCustomDateMaskProperties();

        this.criaEstilosAlertasDeCores();

        super.createsSpecificStylesColumns();
    }

    private void populaMetricasVisualizadas() {
        for (MetricMetaData metaData : this.cube.getHierarchyMetric()) {
            if (metaData.isViewed()) {
                this.visibleMetrics.add(metaData);
                this.metricsAmount++;
            }
        }
    }

    private void createCustomDateMaskProperties() {
        String mask;

        if (this.impressor instanceof ImpressorExcel) {
            for (DimensaoMetaData metaData : this.cube.getHierarchyLine()) {
                if (metaData.getTipo() instanceof TipoData) {

                    mask = metaData.getCampoMetadadata().getMascarasCampo().get(0).getMascara().replace("'", "");

                    CellProperty cellPropertyDataMetrica1 = new CellProperty();
                    cellPropertyDataMetrica1.setAlignment(CellProperty.ALINHAMENTO_DIREITA);
                    cellPropertyDataMetrica1.setFontColor("000080");
                    cellPropertyDataMetrica1.setBackGroundColor("D7E3F7");
                    cellPropertyDataMetrica1.setFontName("Verdana");
                    cellPropertyDataMetrica1.setFontSize(10);
                    //propriedadeCelulaDataMetrica1.addOutroAtributo("white-space", "nowrap");
                    cellPropertyDataMetrica1.setBorderColor("3377CC");
                    cellPropertyDataMetrica1.setSpecificBorder(true);
                    cellPropertyDataMetrica1.setDateMask(mask);
                    this.impressor.adicionaEstilo(cellPropertyDataMetrica1, metaData.getTitle() + "1");

                    CellProperty cellPropertyDataMetrica2 = new CellProperty();
                    cellPropertyDataMetrica2.setAlignment(CellProperty.ALINHAMENTO_DIREITA);
                    cellPropertyDataMetrica2.setFontColor("000080");
                    cellPropertyDataMetrica2.setBackGroundColor("FFFFFF");
                    cellPropertyDataMetrica2.setFontName("Verdana");
                    cellPropertyDataMetrica2.setFontSize(10);
                    //propriedadeCelulaDataMetrica2.addOutroAtributo("white-space", "nowrap");
                    cellPropertyDataMetrica2.setBorderColor("3377CC");
                    cellPropertyDataMetrica2.setDateMask(mask);
                    cellPropertyDataMetrica2.setSpecificBorder(true);
                    this.impressor.adicionaEstilo(cellPropertyDataMetrica2, metaData.getTitle() + "2");

                    this.currentLineCellProperties.get(0).put(metaData.getTitle(), metaData.getTitle() + "1");
                    this.currentLineCellProperties.get(1).put(metaData.getTitle(), metaData.getTitle() + "2");
                }
            }
        }
    }

    private void populaMapPropriedades() {
        Map<String, String> propriedades1 = new HashMap<>();
        Map<String, String> propriedades2 = new HashMap<>();

        for (DimensaoMetaData metaData : this.cube.getHierarchyLine()) {
            if (metaData.getTipo() instanceof TipoData) {
                propriedades1.put(metaData.getTitle(), CellProperty.PROPRIEDADE_CELULA_DATA_METRICA1);
                propriedades2.put(metaData.getTitle(), CellProperty.PROPRIEDADE_CELULA_DATA_METRICA2);
            } else {
                propriedades1.put(metaData.getTitle(), CellProperty.PROPRIEDADE_CELULA_VALOR_METRICA1);
                propriedades2.put(metaData.getTitle(), CellProperty.PROPRIEDADE_CELULA_VALOR_METRICA2);
            }
        }
        this.currentLineCellProperties.put(0, propriedades1);
        this.currentLineCellProperties.put(1, propriedades2);
    }

    private String buscaPropriedadeAplicarCelula(String propriedadeAplicar, Object valor, ColumnMetaData metaData) {
        String propriedadeAlertaMetrica = null;
        List<?> alertasCores = metaData.getColorAlertCells();
        for (Object oAlerta : alertasCores) {
            ColorAlertConditions condicaoAlertaCor = (ColorAlertConditions) oAlerta;
            if (condicaoAlertaCor.testCondition(valor)) {
                propriedadeAlertaMetrica = CellProperty.PROPRIEDADE_CELULA_ALERTAS_PREFIXO + condicaoAlertaCor.getSequence();
            }
        }
        if (propriedadeAlertaMetrica != null) {
            propriedadeAplicar = propriedadeAlertaMetrica;
        }
        return propriedadeAplicar;
    }

    private void imprimeLinha(Map<String, String> mapPropriedades) {
        for (ColumnMetaData coluna : this.cube.getColumnsViewed()) {
            coluna.printFieldTypeValue(this.currentLineValues.get(coluna.getTitle()),
                    mapPropriedades.get(coluna.getTitle()), impressor);
        }
    }

    private void imprimeCabecalho() {
        if (this.hasSequence) {
            this.impressor.imprimeCabecalhoColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_SEQUENCIA, "Seq");
        }
        for (ColumnMetaData coluna : this.cube.getColumnsViewed()) {
            this.impressor.imprimeCabecalhoColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_PADRAO, coluna);
        }
    }

    private void mergulhaNivelLinha(Dimension dimensionLinha) {
        this.openLine();
        DimensaoMetaData metaData = dimensionLinha.getMetaData();
        Dimension dimension = dimensionLinha;
        while (metaData != null) {
            currentLineValues.put(metaData.getTitle(), dimension.getVisualizationValue());
            metaData = metaData.getParent();
            dimension = dimension.getParent();
        }
        this.imprimeLinhaAtual(dimensionLinha);
        this.impressor.fechaLinha();
    }

    private void imprimeLinhaAtual(Dimension dimensionLinha) {
        MetricsMap metricsMap = this.cube.getMetricsMap();
        MetricLine metricLine = metricsMap.getMetricLine(dimensionLinha);

        Map<String, Metric> metricas = metricLine.getMetrics();
        String propriedadeLinhaAtual = dimensionLinha.getMetricDefaultStyles(this.currentLine);

        Map<String, String> mapPropriedades = this.currentLineCellProperties.get(this.currentLine % 2);
        for (MetricMetaData metricMetaData : this.visibleMetrics) {
            String titulo = metricMetaData.getTitle();
            Double valor = metricas.get(titulo).getValor(metricsMap, metricLine, this.previousMetricLine);
            this.currentLineValues.put(titulo, valor);
            String propriedadeAplicarMetrica = this.buscaPropriedadeAplicarCelula(propriedadeLinhaAtual, valor, metricMetaData);
            mapPropriedades.put(titulo, propriedadeAplicarMetrica);
        }
        String sequenciaRanking = Optional.ofNullable(dimensionLinha.getRankingSequence()).map(String::valueOf).orElse(this.impressor.getValorVazio());
        if (hasSequence) {
            this.impressor.imprimeCampoSequencia(sequenciaRanking);
        }
        this.imprimeLinha(mapPropriedades);

        this.previousMetricLine = metricLine;

    }

}