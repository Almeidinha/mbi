package com.msoft.mbi.cube.multi.generation;

import java.util.*;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.column.ColunaMetaData;
import com.msoft.mbi.cube.multi.column.TipoData;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditions;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionColunaNula;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.metrics.Metrica;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class DefaultTableBuilder extends tableGenerator {

    private Map<String, Object> currentLineValues = new HashMap<>();
    private final Map<Integer, Map<String, String>> currentLineCellProperties = new HashMap<>();
    private boolean hasSequence = false;
    private MetricLine previousMetricLine = null;

    public DefaultTableBuilder(Cubo cubo) {
        this.cube = cubo;
        this.metricsAmount = 0;
        this.visibleMetrics = new ArrayList<>();
        this.populaMetricasVisualizadas();
        this.populaMapPropriedades();
        this.hasSequence = this.cube.getColunasVisualizadas().get(0).hasCampoSequencia();
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
        List<Dimension> dimensoes = this.cube.getDimensoesUltimoNivelLinha();
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
        for (MetricaMetaData metricaMetaData : this.visibleMetrics) {
            String titulo = metricaMetaData.getTitulo();
            if (metricaMetaData.isTotalizarLinhas()) {
                Double valor = metricaMetaData.calculaValorTotalParcial(this.cube, dimensaoColunaNula);
                this.currentLineValues.put(titulo, valor);
            }
        }
        this.openLine();
        if (hasSequence) {
            impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_TOTALGERAL, this.impressor.getValorVazio());
        }

        for (ColunaMetaData coluna : this.cube.getColunasVisualizadas()) {
            if (this.currentLineValues.containsKey(coluna.getTitulo())) {
                coluna.imprimeValorTipoCampo(this.currentLineValues.get(coluna.getTitulo()), CellProperty.PROPRIEDADE_CELULA_TOTALGERAL, impressor);
            } else {
                this.impressor.imprimeColuna(CellProperty.PROPRIEDADE_CELULA_TOTALGERAL, this.impressor.getValorVazio());
            }
        }
        this.impressor.fechaLinha();
    }

    private void criaEstilosAlertasDeCores() {
        for (MetricaMetaData metaData : this.cube.getHierarquiaMetrica()) {
            this.createColorAlertStyles(metaData.getAlertasCoresCelula());
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
        for (MetricaMetaData metaData : this.cube.getHierarquiaMetrica()) {
            if (metaData.isVisualizada()) {
                this.visibleMetrics.add(metaData);
                this.metricsAmount++;
            }
        }
    }

    private void createCustomDateMaskProperties() {
        String mask;

        if (this.impressor instanceof ImpressorExcel) {
            for (DimensaoMetaData metaData : this.cube.getHierarquiaLinha()) {
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
                    this.impressor.adicionaEstilo(cellPropertyDataMetrica1, metaData.getTitulo() + "1");

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
                    this.impressor.adicionaEstilo(cellPropertyDataMetrica2, metaData.getTitulo() + "2");

                    this.currentLineCellProperties.get(0).put(metaData.getTitulo(), metaData.getTitulo() + "1");
                    this.currentLineCellProperties.get(1).put(metaData.getTitulo(), metaData.getTitulo() + "2");
                }
            }
        }
    }

    private void populaMapPropriedades() {
        Map<String, String> propriedades1 = new HashMap<>();
        Map<String, String> propriedades2 = new HashMap<>();

        for (DimensaoMetaData metaData : this.cube.getHierarquiaLinha()) {
            if (metaData.getTipo() instanceof TipoData) {
                propriedades1.put(metaData.getTitulo(), CellProperty.PROPRIEDADE_CELULA_DATA_METRICA1);
                propriedades2.put(metaData.getTitulo(), CellProperty.PROPRIEDADE_CELULA_DATA_METRICA2);
            } else {
                propriedades1.put(metaData.getTitulo(), CellProperty.PROPRIEDADE_CELULA_VALOR_METRICA1);
                propriedades2.put(metaData.getTitulo(), CellProperty.PROPRIEDADE_CELULA_VALOR_METRICA2);
            }
        }
        this.currentLineCellProperties.put(0, propriedades1);
        this.currentLineCellProperties.put(1, propriedades2);
    }

    private String buscaPropriedadeAplicarCelula(String propriedadeAplicar, Object valor, ColunaMetaData metaData) {
        String propriedadeAlertaMetrica = null;
        List<?> alertasCores = metaData.getAlertasCoresCelula();
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
        for (ColunaMetaData coluna : this.cube.getColunasVisualizadas()) {
            coluna.imprimeValorTipoCampo(this.currentLineValues.get(coluna.getTitulo()),
                    mapPropriedades.get(coluna.getTitulo()), impressor);
        }
    }

    private void imprimeCabecalho() {
        if (this.hasSequence) {
            this.impressor.imprimeCabecalhoColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_SEQUENCIA, "Seq");
        }
        for (ColunaMetaData coluna : this.cube.getColunasVisualizadas()) {
            this.impressor.imprimeCabecalhoColuna(CellProperty.PROPRIEDADE_CELULA_CABECALHO_PADRAO, coluna);
        }
    }

    private void mergulhaNivelLinha(Dimension dimensionLinha) {
        this.openLine();
        DimensaoMetaData metaData = dimensionLinha.getMetaData();
        Dimension dimension = dimensionLinha;
        while (metaData != null) {
            currentLineValues.put(metaData.getTitulo(), dimension.getVisualizationValue());
            metaData = metaData.getParent();
            dimension = dimension.getParent();
        }
        this.imprimeLinhaAtual(dimensionLinha);
        this.impressor.fechaLinha();
    }

    private void imprimeLinhaAtual(Dimension dimensionLinha) {
        MapaMetricas mapaMetricas = this.cube.getMapaMetricas();
        MetricLine metricLine = mapaMetricas.getMetricLine(dimensionLinha);

        Map<String, Metrica> metricas = metricLine.getMetrics();
        String propriedadeLinhaAtual = dimensionLinha.getMetricDefaultStyles(this.currentLine);

        Map<String, String> mapPropriedades = this.currentLineCellProperties.get(this.currentLine % 2);
        for (MetricaMetaData metricaMetaData : this.visibleMetrics) {
            String titulo = metricaMetaData.getTitulo();
            Double valor = metricas.get(titulo).getValor(mapaMetricas, metricLine, this.previousMetricLine);
            this.currentLineValues.put(titulo, valor);
            String propriedadeAplicarMetrica = this.buscaPropriedadeAplicarCelula(propriedadeLinhaAtual, valor, metricaMetaData);
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