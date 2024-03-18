package com.msoft.mbi.cube.multi.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.column.ColunaMetaData;
import com.msoft.mbi.cube.multi.column.DataType;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetricaValor;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertProperties;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.generation.Impressor;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.renderers.MascaraCasasDecimaisRenderer;
import com.msoft.mbi.cube.multi.renderers.MascaraValorNulo;
import com.msoft.mbi.cube.multi.partialTotalization.TotalizacaoParcialAplicarTipo;
import com.msoft.mbi.cube.multi.partialTotalization.TotalizacaoParcialAplicarTipoExpressao;
import com.msoft.mbi.cube.multi.partialTotalization.TotalizacaoParcialAplicarTipoSoma;
import lombok.Getter;
import lombok.Setter;

public abstract class MetricaMetaData extends ColunaMetaData {

    @Setter
    @Getter
    private DataType<Double> tipo;
    @Getter
    @Setter
    private Cubo cubo = null;
    @Getter
    @Setter
    private boolean visualizada = true;
    @Getter
    @Setter
    private boolean totalizarParcialLinhas = false;
    @Getter
    @Setter
    private boolean mediaParcialLinhas = false;
    @Setter
    @Getter
    private boolean expressaoParcialLinhas = false;
    @Setter
    @Getter
    private boolean totalizarParcialColunas = false;
    @Getter
    @Setter
    private boolean mediaParcialColunas = false;
    @Setter
    @Getter
    private boolean expressaoParcialColunas = false;
    @Setter
    @Getter
    private boolean totalizarLinhas = false;
    @Setter
    @Getter
    private boolean totalizarSomaColunas = false;
    @Getter
    @Setter
    private boolean totalizarMediaColunas = false;
    @Getter
    @Setter
    private boolean totalizarSomaGeralColunas = false;
    @Getter
    @Setter
    private boolean utilizaPercentual = false;
    private MascaraCasasDecimaisRenderer casasDecimaisRenderer;
    @Setter
    private MascaraValorNulo formatacaoValorNulo;
    private final Map<String, List<ColorAlertConditionsMetrica>> mapAlertasCoresLinha;
    private final Map<String, List<ColorAlertConditionsMetrica>> mapAlertasCoresCelula;
    private TotalizacaoParcialAplicarTipo tipoAcumuladoParcialCalculator;
    @Getter
    private int numeroCasasDecimais = 0;
    public static final String AGREGACAO_SOMATORIO = "SUM";
    public static final String AGREGACAO_CONTAGEM = "COUNT";
    public static final String AGREGACAO_MEDIA = "AVG";
    public static final String AGREGACAO_MINIMO = "MIN";
    public static final String AGREGACAO_MAXIMO = "MAX";
    public static final String AGREGACAO_VAZIO = "VAZIO";
    @Getter
    private final List<OrdenacaoMetrica> ordenacoesMetrica;
    @Setter
    protected String agregacaoTipo;
    public static final String TOTALIZACAO_AH = "totalizacaoHorizontal";
    public static final String MEDIA_AH = "mediaHorizontal";
    public static final String VALOR_ACUMULADO_AH = "acumuladoHorizontal";
    public static final String TOTALIZACAO_AV = "totalizacaoVertical";
    public static final String TOTALIZACAO_PARCIAL = "totalizacaoParcial";
    public static final String MEDIA_PARCIAL = "mediaParcial";
    public static final String EXPRESSAO_PARCIAL = "expressaoParcial";
    public static final String TOTALIZACAO_GERAL = "totalizacao";

    protected MetricaMetaData(String titulo, DataType<Double> tipo) {
        super(titulo, CampoMetaData.METRICA);
        this.tipo = tipo;
        this.mapAlertasCoresCelula = new HashMap<>();
        this.mapAlertasCoresLinha = new HashMap<>();
        this.formatacaoValorNulo = new MascaraValorNulo();
        this.casasDecimaisRenderer = new MascaraCasasDecimaisRenderer();
        this.tipoAcumuladoParcialCalculator = TotalizacaoParcialAplicarTipoSoma.getInstance();
        this.ordenacoesMetrica = new ArrayList<>();
        this.agregacaoTipo = AGREGACAO_SOMATORIO;
    }

    public static void factory(MetricaMetaData metricaMetaData, CampoMetaData campoMetaData) {
        ColunaMetaData.factory(metricaMetaData, campoMetaData);
        if (campoMetaData.getOrdem() > 0) {
            OrdenacaoMetrica ordenacaoMetrica = new OrdenacaoMetrica(campoMetaData.getSentidoOrdem(), campoMetaData.getOrdem(),
                    metricaMetaData.getTitulo(), MetricaValorUtilizarLinhaMetrica.getInstance());
            metricaMetaData.addOrdenacaoMetrica(ordenacaoMetrica);
        }
        if (campoMetaData.getOrdemAcumulado() > 0) {
            OrdenacaoMetrica ordenacaoMetrica = new OrdenacaoMetrica(campoMetaData.getSentidoOrdemAcumulado(), campoMetaData.getOrdemAcumulado(),
                    metricaMetaData.getTitulo(), MetricaValorUtilizarTotal.getInstance());
            metricaMetaData.addOrdenacaoMetrica(ordenacaoMetrica);
        }
        if (campoMetaData.getMascaraValorNulo() != null) {
            metricaMetaData.setFormatacaoValorNulo(new MascaraValorNulo(campoMetaData.getMascaraValorNulo()));
        }
        factoryAlertasCores(metricaMetaData, campoMetaData.getAlertasCoresValor());

        metricaMetaData.setNCasasDecimais(campoMetaData.getNumPosDecimais());
        metricaMetaData.setTotalizarSomaColunas(campoMetaData.isAcumulaCampoLinha());
        metricaMetaData.setTotalizarSomaGeralColunas(campoMetaData.isTotalizaCampoLinha());
        metricaMetaData.setTotalizarLinhas(campoMetaData.isTotalizaCampo());
        metricaMetaData.setTotalizarParcialLinhas(campoMetaData.isTotalizacaoParcial());
        metricaMetaData.setTotalizarParcialColunas(campoMetaData.isTotalizacaoParcial());

        metricaMetaData.setMediaParcialColunas(campoMetaData.isMediaParcial());
        metricaMetaData.setMediaParcialLinhas(campoMetaData.isMediaParcial());
        metricaMetaData.setExpressaoParcialColunas(campoMetaData.isExpressaoNaParcial());
        metricaMetaData.setExpressaoParcialLinhas(campoMetaData.isExpressaoNaParcial());
        metricaMetaData.setExpressaoParcialTotal(campoMetaData.isExpressaoNaParcialTotal());
        metricaMetaData.setTotalizarMediaColunas(campoMetaData.isUtilizaMediaLinha());
        metricaMetaData.setAgregacaoTipo(campoMetaData.getAgregacaoTipo());
    }

    protected static void factoryAlertasCores(MetricaMetaData metricaMetaData, List<AlertaCorMetaData> alertas) {
        ColorAlertProperties propriedadeAlerta;
        ColorAlertConditionsMetricaValor condicaoAlertaCores;
        for (AlertaCorMetaData alertaCor : alertas) {
            propriedadeAlerta = ColorAlertProperties.factory(alertaCor.getCorFonte(), alertaCor.getCorFundo(), alertaCor.getEstiloFonte(),
                    alertaCor.isNegrito(), alertaCor.isItalico(), alertaCor.getTamanhoFonte());
            propriedadeAlerta.setAlignment(ColorAlertProperties.ALINHAMENTO_DIREITA);
            condicaoAlertaCores = new ColorAlertConditionsMetricaValor(alertaCor.getSequencia(), propriedadeAlerta, alertaCor.getFuncao(),
                    alertaCor.getAcao(), alertaCor.getOperador(), metricaMetaData, alertaCor.getValores());
            metricaMetaData.addAlertaCor(condicaoAlertaCores);
        }
    }

    public List<ColorAlertConditionsMetrica> getAlertasCoresLinha() {
        return getAlertasCores(this.mapAlertasCoresLinha);
    }

    public List<ColorAlertConditionsMetrica> getAlertasCoresCelula() {
        return getAlertasCores(this.mapAlertasCoresCelula);
    }

    public List<ColorAlertConditionsMetrica> getAlertasCoresLinha(String funcao) {
        return mapAlertasCoresLinha.get(funcao);
    }

    public List<ColorAlertConditionsMetrica> getAlertasCoresCelula(String funcao) {
        return mapAlertasCoresCelula.get(funcao);
    }

    protected void setTipoTotalizacaoLinhas(String totalizacao) {
        if (CampoMetaData.TOTALIZAR_APLICAR_SOMA.equals(totalizacao)) {
            this.tipoAcumuladoParcialCalculator = TotalizacaoParcialAplicarTipoSoma.getInstance();
        } else {
            this.tipoAcumuladoParcialCalculator = TotalizacaoParcialAplicarTipoExpressao.getInstance();
        }
    }

    public int getNCasasDecimais() {
        return this.casasDecimaisRenderer.getNCasasDecimais();
    }

    public void setNCasasDecimais(int casasDecimais) {
        this.numeroCasasDecimais = casasDecimais;
        this.casasDecimaisRenderer = new MascaraCasasDecimaisRenderer(this.numeroCasasDecimais);
    }

    @Override
    public String getTextoValorNulo() {
        return this.formatacaoValorNulo.getTexto();
    }

    public void addOrdenacaoMetrica(OrdenacaoMetrica ordenacaoMetrica) {
        this.ordenacoesMetrica.add(ordenacaoMetrica);
    }

    public String getFormattedValue(Object valor) {
        if (valor != null) {
            return super.getFormattedValue(this.casasDecimaisRenderer.aplica(valor));
        } else {
            return this.getTextoValorNulo();
        }
    }

    private List<ColorAlertConditionsMetrica> getAlertasCores(Map<String, List<ColorAlertConditionsMetrica>> alertas) {
        List<ColorAlertConditionsMetrica> retorno = new ArrayList<>();
        for (List<ColorAlertConditionsMetrica> colorAlertConditionsMetricas : alertas.values()) {
            retorno.addAll(colorAlertConditionsMetricas);
        }
        return retorno;
    }

    private void addAlertaCorLinha(ColorAlertConditionsMetrica alertaCor) {
        addAlertaCor(alertaCor, this.mapAlertasCoresLinha);
    }

    private void addAlertaCor(ColorAlertConditionsMetrica alertaCor, Map<String, List<ColorAlertConditionsMetrica>> mapAlertas) {
        String funcao = alertaCor.getFunction();
        List<ColorAlertConditionsMetrica> alertas = mapAlertas.get(funcao);
        if (alertas == null) {
            alertas = new ArrayList<>();
        }
        alertas.add(alertaCor);
        mapAlertas.put(funcao, alertas);
    }

    private void addAlertaCorCelula(ColorAlertConditionsMetrica alertaCor) {
        addAlertaCor(alertaCor, this.mapAlertasCoresCelula);
    }

    public void addAlertaCor(ColorAlertConditionsMetrica alertaCor) {
        if (AlertaCorMetaData.ACAO_PINTAR_LINHA == alertaCor.getAction()) {
            this.addAlertaCorLinha(alertaCor);
        } else {
            this.addAlertaCorCelula(alertaCor);
        }
    }

    @Override
    public String toString() {
        return this.getTitulo();
    }

    public Double calculaValorTotalParcial(Dimension dimensionEixoReferencia, Dimension dimension) {
        return this.tipoAcumuladoParcialCalculator.calculaValor(dimensionEixoReferencia, dimension, this, this.cubo.getMapaMetricas());
    }

    @Override
    public void imprimeValorTipoCampo(Object valor, String propriedadeCelula, Impressor impressor) {
        impressor.imprimeValorMetrica(propriedadeCelula, ((Double) valor), this);
    }

    public abstract Metrica createMetrica();

}
