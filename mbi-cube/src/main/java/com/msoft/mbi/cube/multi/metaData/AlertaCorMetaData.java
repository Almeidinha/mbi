package com.msoft.mbi.cube.multi.metaData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAcumuladoParticipacaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAcumuladoParticipacaoAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAcumuladoValorAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedEvolucaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoAVMetaData;
import lombok.Setter;

public class AlertaCorMetaData implements Serializable {

    public static final int ACAO_PINTAR_LINHA = 1;
    public static final int ACAO_PINTAR_CELULA = 2;
    @Setter
    private int sequencia;
    @Setter
    private int acao;
    @Setter
    private String operador;
    @Setter
    private String corFonte;
    @Setter
    private String corFundo;
    @Setter
    private String estiloFonte;
    @Setter
    private boolean negrito;
    @Setter
    private boolean italico;
    @Setter
    private int tamanhoFonte;
    @Setter
    private String funcao;
    @Setter
    private List<Object> valores;
    private String tipoComparacao;
    private Double valorReferencia;
    private String tituloOutroCampo;
    private String funcaoCampoDestino;
    public static final String SEM_FUNCAO = "semFuncao";
    public static final String TIPO_ALERTA_VALOR = "tipoAlertaValor";
    public static final String TIPO_ALERTA_OUTRO_CAMPO = "tipoAlertaOutroCampo";
    public static final String TIPO_COMPARACAO_VALOR = "V";
    public static final String TIPO_COMPARACAO_PERCENTUAL = "P";
    private static final List<String> FUNCOES_AH = new ArrayList<>();

    public AlertaCorMetaData(int sequencia, String operador, Object valor, int acao, String funcao, String corFonte, String corFundo,
                             String estiloFonte, boolean negrito, boolean italico, int tamanhoFonte) {
        this(sequencia, operador, new ArrayList<Object>(), acao, corFonte, corFundo, estiloFonte, negrito, italico, tamanhoFonte, funcao);
        valores.add(valor);
    }

    public AlertaCorMetaData(int sequencia, String operador, Object valor1, Object valor2, int acao, String funcao, String corFonte, String corFundo,
                             String estiloFonte, boolean negrito, boolean italico, int tamanhoFonte) {
        this(sequencia, operador, new ArrayList<Object>(), acao, corFonte, corFundo, estiloFonte, negrito, italico, tamanhoFonte, funcao);
        if (valor1 != null)
            valores.add(valor1);
        if (valor2 != null)
            valores.add(valor2);
    }

    public AlertaCorMetaData(int sequencia, String operador, Double valorReferencia, int acao, String funcao, String corFonte, String corFundo,
                             String estiloFonte, boolean negrito, boolean italico, int tamanhoFonte, String tipoComparacao, String tituloOutroCampo,
                             String funcaoCampoDestino) {
        this(sequencia, operador, new ArrayList<Object>(), acao, corFonte, corFundo, estiloFonte, negrito, italico, tamanhoFonte, funcao);
        this.tipoComparacao = tipoComparacao;
        this.valorReferencia = valorReferencia;
        this.tituloOutroCampo = tituloOutroCampo;
        this.funcaoCampoDestino = funcaoCampoDestino;
    }

    public AlertaCorMetaData(int sequencia, String operador, Object valor1, Object valor2, String corFonte, String corFundo, String estiloFonte,
                             boolean negrito, boolean italico, int tamanhoFonte) {
        this(sequencia, operador, new ArrayList<Object>(), ACAO_PINTAR_CELULA, corFonte, corFundo, estiloFonte, negrito, italico, tamanhoFonte,
                SEM_FUNCAO);
        if (valor1 != null)
            valores.add(valor1);
        if (valor2 != null)
            valores.add(valor2);
    }

    private AlertaCorMetaData(int sequencia, String operador, List<Object> valores, int acao, String corFonte, String corFundo, String estiloFonte,
                              boolean negrito, boolean italico, int tamanhoFonte, String funcao) {
        this.sequencia = sequencia;
        this.acao = acao;
        this.operador = operador;
        this.valores = valores;
        this.corFonte = corFonte;
        this.corFundo = corFundo;
        this.estiloFonte = estiloFonte;
        this.negrito = negrito;
        this.italico = italico;
        this.tamanhoFonte = tamanhoFonte;
        this.funcao = funcao;
        FUNCOES_AH.add(MetricMetaData.TOTAL_AH);
        FUNCOES_AH.add(MetricMetaData.MEDIA_AH);
        FUNCOES_AH.add(MetricMetaData.ACCUMULATED_VALUE_AH);
    }

    public int getSequencia() {
        return sequencia;
    }

    public int getAcao() {
        return acao;
    }

    public String getOperador() {
        return operador;
    }

    public List<Object> getValores() {
        return valores;
    }

    public String getCorFonte() {
        return corFonte;
    }

    public String getCorFundo() {
        return corFundo;
    }

    public String getEstiloFonte() {
        return estiloFonte;
    }

    public boolean isNegrito() {
        return negrito;
    }

    public boolean isItalico() {
        return italico;
    }

    public int getTamanhoFonte() {
        return tamanhoFonte;
    }

    public String getFuncao() {
        return funcao;
    }

    public boolean isFuncaoCampoRelativo() {
        return this.funcao.equals(MetricCalculatedParticipacaoAVMetaData.AV)
                || this.funcao.equals(MetricCalculatedAcumuladoParticipacaoAVMetaData.PARTICIPACAO_ACUMULADA_AV)
                || this.funcao.equals(MetricCalculatedAcumuladoValorAVMetaData.VALOR_ACUMULADO_AV)
                || this.funcao.equals(MetricCalculatedAcumuladoParticipacaoAHMetaData.PARTICIPACAO_ACUMULADA_AH)
                || this.funcao.equals(MetricCalculatedParticipacaoAHMetaData.PARTICIPACAO_AH)
                || this.funcao.equals(MetricCalculatedEvolucaoAHMetaData.AH);
    }

    public static List<String> getListaFuncoesTotalizacaoHorizontal() {

        return new ArrayList<>(FUNCOES_AH);
    }

    public String getTipoComparacao() {
        return tipoComparacao;
    }

    public Double getValorReferencia() {
        return valorReferencia;
    }

    public String getTituloOutroCampo() {
        return tituloOutroCampo;
    }

    public String getFuncaoCampoDestino() {
        return funcaoCampoDestino;
    }
}
