package com.msoft.mbi.cube.multi.metrics;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public abstract class MetricaMetaData extends ColunaMetaData {

    @Serial
    private static final long serialVersionUID = 4469002703551885025L;
    private DataType<Double> tipo = null;
    private Cubo cubo = null;
    private boolean visualizada = true;
    private boolean totalizarParcialLinhas = false;
    private boolean mediaParcialLinhas = false;
    private boolean expressaoParcialLinhas = false;
    private boolean totalizarParcialColunas = false;
    private boolean mediaParcialColunas = false;
    private boolean expressaoParcialColunas = false;
    private boolean totalizarLinhas = false;
    private boolean totalizarSomaColunas = false;
    private boolean totalizarMediaColunas = false;
    private boolean totalizarSomaGeralColunas = false;
    private boolean utilizaPercentual = false;
    private MascaraCasasDecimaisRenderer casasDecimaisRenderer;
    private MascaraValorNulo formatacaoValorNulo = null;
    private Map<String, List<ColorAlertConditionsMetrica>> mapAlertasCoresLinha;
    private Map<String, List<ColorAlertConditionsMetrica>> mapAlertasCoresCelula;
    private TotalizacaoParcialAplicarTipo tipoAcumuladoParcialCalculator;
    private int numeroCasasDecimais = 0;
    public static final String AGREGACAO_SOMATORIO = "SUM";
    public static final String AGREGACAO_CONTAGEM = "COUNT";
    public static final String AGREGACAO_MEDIA = "AVG";
    public static final String AGREGACAO_MINIMO = "MIN";
    public static final String AGREGACAO_MAXIMO = "MAX";
    public static final String AGREGACAO_VAZIO = "VAZIO";
    private List<OrdenacaoMetrica> ordenacoesMetrica;
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
        ColorAlertProperties propriedadeAlerta = null;
        ColorAlertConditionsMetricaValor condicaoAlertaCores = null;
        for (AlertaCorMetaData alertaCor : alertas) {
            propriedadeAlerta = ColorAlertProperties.factory(alertaCor.getCorFonte(), alertaCor.getCorFundo(), alertaCor.getEstiloFonte(),
                    alertaCor.isNegrito(), alertaCor.isItalico(), alertaCor.getTamanhoFonte());
            propriedadeAlerta.setAlignment(ColorAlertProperties.ALINHAMENTO_DIREITA);
            condicaoAlertaCores = new ColorAlertConditionsMetricaValor(alertaCor.getSequencia(), propriedadeAlerta, alertaCor.getFuncao(),
                    alertaCor.getAcao(), alertaCor.getOperador(), metricaMetaData, alertaCor.getValores());
            metricaMetaData.addAlertaCor(condicaoAlertaCores);
        }
    }

    public DataType<Double> getTipo() {
        return tipo;
    }

    public void setTipo(DataType<Double> tipo) {
        this.tipo = tipo;
    }

    public Cubo getCubo() {
        return cubo;
    }

    public void setCubo(Cubo cubo) {
        this.cubo = cubo;
    }

    public boolean isVisualizada() {
        return visualizada;
    }

    public void setVisualizada(boolean visualizada) {
        this.visualizada = visualizada;
    }

    public boolean isTotalizarParcialLinhas() {
        return totalizarParcialLinhas;
    }

    public void setTotalizarParcialLinhas(boolean totalizacaoParcial) {
        this.totalizarParcialLinhas = totalizacaoParcial;
    }

    public boolean isMediaParcialLinhas() {
        return mediaParcialLinhas;
    }

    public void setMediaParcialLinhas(boolean mediaParcialLinhas) {
        this.mediaParcialLinhas = mediaParcialLinhas;
    }

    public boolean isExpressaoParcialLinhas() {
        return expressaoParcialLinhas;
    }

    public void setExpressaoParcialLinhas(boolean expressaoParcialLinhas) {
        this.expressaoParcialLinhas = expressaoParcialLinhas;
    }

    public void setFormatacaoValorNulo(MascaraValorNulo formatacaoValorNulo) {
        this.formatacaoValorNulo = formatacaoValorNulo;
    }

    public boolean isTotalizarLinhas() {
        return totalizarLinhas;
    }

    public void setTotalizarLinhas(boolean totalizar) {
        this.totalizarLinhas = totalizar;
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

    public boolean isTotalizarSomaColunas() {
        return totalizarSomaColunas;
    }

    public void setTotalizarSomaColunas(boolean totalizarColunas) {
        this.totalizarSomaColunas = totalizarColunas; // mudar para aceitar o tipo de totalizacao
    }

    public boolean isTotalizarSomaGeralColunas() {
        return totalizarSomaGeralColunas;
    }

    public void setTotalizarSomaGeralColunas(boolean totalizarGeralMetricasColunas) {
        this.totalizarSomaGeralColunas = totalizarGeralMetricasColunas;
    }

    public int getNCasasDecimais() {
        return this.casasDecimaisRenderer.getNCasasDecimais();
    }

    public void setNCasasDecimais(int casasDecimais) {
        this.numeroCasasDecimais = casasDecimais;
        this.casasDecimaisRenderer = new MascaraCasasDecimaisRenderer(this.numeroCasasDecimais);
    }

    public boolean isTotalizarParcialColunas() {
        return totalizarParcialColunas;
    }

    public void setTotalizarParcialColunas(boolean totalizarParcialColunas) {
        this.totalizarParcialColunas = totalizarParcialColunas;
    }

    public boolean isMediaParcialColunas() {
        return mediaParcialColunas;
    }

    public void setMediaParcialColunas(boolean mediaParcialColunas) {
        this.mediaParcialColunas = mediaParcialColunas;
    }

    public boolean isExpressaoParcialColunas() {
        return expressaoParcialColunas;
    }

    public void setExpressaoParcialColunas(boolean expressaoParcialColunas) {
        this.expressaoParcialColunas = expressaoParcialColunas;
    }

    public boolean isTotalizarMediaColunas() {
        return totalizarMediaColunas;
    }

    public void setTotalizarMediaColunas(boolean totalizarMediaColunas) {
        this.totalizarMediaColunas = totalizarMediaColunas;
    }

    public List<OrdenacaoMetrica> getOrdenacoesMetrica() {
        return ordenacoesMetrica;
    }

    public void setAgregacaoTipo(String agregacaoTipo) {
        this.agregacaoTipo = agregacaoTipo;
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
            return super.getFormattedValue(this.casasDecimaisRenderer.aplica(valor)).toString();
        } else {
            return this.getTextoValorNulo();
        }
    }

    private List<ColorAlertConditionsMetrica> getAlertasCores(Map<String, List<ColorAlertConditionsMetrica>> alertas) {
        List<ColorAlertConditionsMetrica> retorno = new ArrayList<>();
        Iterator<List<ColorAlertConditionsMetrica>> iListasAlertas = alertas.values().iterator();
        while (iListasAlertas.hasNext()) {
            retorno.addAll(iListasAlertas.next());
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

    public void setUtilizaPercentual(boolean utilizaPercentual) {
        this.utilizaPercentual = utilizaPercentual;
    }

    public boolean isUtilizaPercentual() {
        return utilizaPercentual;
    }

    @Override
    public String toString() {
        return this.getTitulo();
    }

    public Double calculaValorTotalParcial(Dimension dimensionEixoReferencia, Dimension dimension) {
        return this.tipoAcumuladoParcialCalculator.calculaValor(dimensionEixoReferencia, dimension, this, this.cubo.getMapaMetricas());
    }

    public int getNumeroCasasDecimais() {
        return numeroCasasDecimais;
    }

    @Override
    public void imprimeValorTipoCampo(Object valor, String propriedadeCelula, Impressor impressor) {
        impressor.imprimeValorMetrica(propriedadeCelula, ((Double) valor), this);
    }

    public abstract Metrica createMetrica();

}
