package com.msoft.mbi.cube.multi.metaData;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaAcumuladoParticipacaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaAcumuladoParticipacaoAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaAcumuladoValorAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaEvolucaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaParticipacaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaParticipacaoAVMetaData;

public class AlertaCorMetaData implements Serializable {

    @Serial
    private static final long serialVersionUID = -9205019701541912864L;

    public static final int ACAO_PINTAR_LINHA = 1;
    public static final int ACAO_PINTAR_CELULA = 2;
    private int sequencia;
    private int acao;
    private String operador;
    private String corFonte;
    private String corFundo;
    private String estiloFonte;
    private boolean negrito;
    private boolean italico;
    private int tamanhoFonte;
    private String funcao;
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
        FUNCOES_AH.add(MetricaMetaData.TOTALIZACAO_AH);
        FUNCOES_AH.add(MetricaMetaData.MEDIA_AH);
        FUNCOES_AH.add(MetricaMetaData.VALOR_ACUMULADO_AH);
    }

    public int getSequencia() {
        return sequencia;
    }

    public void setSequencia(int sequencia) {
        this.sequencia = sequencia;
    }

    public int getAcao() {
        return acao;
    }

    public void setAcao(int acao) {
        this.acao = acao;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public List<Object> getValores() {
        return valores;
    }

    public void setValores(List<Object> valores) {
        this.valores = valores;
    }

    public String getCorFonte() {
        return corFonte;
    }

    public void setCorFonte(String corFonte) {
        this.corFonte = corFonte;
    }

    public String getCorFundo() {
        return corFundo;
    }

    public void setCorFundo(String corFundo) {
        this.corFundo = corFundo;
    }

    public String getEstiloFonte() {
        return estiloFonte;
    }

    public void setEstiloFonte(String estiloFonte) {
        this.estiloFonte = estiloFonte;
    }

    public boolean isNegrito() {
        return negrito;
    }

    public void setNegrito(boolean negrito) {
        this.negrito = negrito;
    }

    public boolean isItalico() {
        return italico;
    }

    public void setItalico(boolean italico) {
        this.italico = italico;
    }

    public int getTamanhoFonte() {
        return tamanhoFonte;
    }

    public void setTamanhoFonte(int tamanhoFonte) {
        this.tamanhoFonte = tamanhoFonte;
    }

    public String getFuncao() {
        return funcao;
    }

    public boolean isFuncaoCampoRelativo() {
        return this.funcao.equals(MetricaCalculadaParticipacaoAVMetaData.AV)
                || this.funcao.equals(MetricaCalculadaAcumuladoParticipacaoAVMetaData.PARTICIPACAO_ACUMULADA_AV)
                || this.funcao.equals(MetricaCalculadaAcumuladoValorAVMetaData.VALOR_ACUMULADO_AV)
                || this.funcao.equals(MetricaCalculadaAcumuladoParticipacaoAHMetaData.PARTICIPACAO_ACUMULADA_AH)
                || this.funcao.equals(MetricaCalculadaParticipacaoAHMetaData.PARTICIPACAO_AH)
                || this.funcao.equals(MetricaCalculadaEvolucaoAHMetaData.AH);
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public static List<String> getListaFuncoesTotalizacaoHorizontal() {
        List<String> lst = new ArrayList<>();
        lst.addAll(FUNCOES_AH);

        return lst;
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
