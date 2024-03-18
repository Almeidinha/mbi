package com.msoft.mbi.cube.multi.metaData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.msoft.mbi.cube.multi.column.MascaraColunaMetaData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("unused")
public class CampoMetaData implements Serializable {

    private Integer campo;
    private String nomeCampo;
    private String tituloCampo;
    private String tipoDado;
    private String tipoCampo;
    private String apelidoCampo;
    private boolean expressao;
    private String padrao;
    private CampoMetaData campoOrdenacao;
    private Integer ordem;
    private String apelidoTabela;
    private String sentidoOrdem;
    private Integer numPosDecimais;
    private boolean totalizaCampo;
    private String analiseVerticalTipo;
    private boolean participacaoAcumulada;
    private boolean valorAcumulado;
    private Integer localApresentacao;
    private Integer larguraColuna = 100;
    private String posicaoAlinhamentoColuna;
    private String analiseHorizontalTipo;
    private boolean totalizaCampoLinha;
    private boolean acumulaCampoLinha;
    private boolean totalizacaoParcial;
    private boolean mediaParcial;
    private boolean expressaoNaParcial;
    private boolean expressaoNaParcialTotal;
    private Integer qtdPasso;
    private boolean participacaoHorizontal;
    private boolean participacaoAcumuladaHorizontal;
    private Integer ordemAcumulado;
    private String sentidoOrdemAcumulado;
    private boolean utilizaMediaLinha;
    private boolean drillDown;
    private String agregacaoTipo;
    private boolean mostraSequencia;
    private String expressaoRanking;
    private List<MascaraColunaMetaData> mascarasCampo;
    private List<MascaraLinkHTMLMetaData> mascarasLinkHTML;
    private String tipoTotalizacaoLinhas;
    private Integer sequencia;
    private String mascaraValorNulo;
    private List<AlertaCorMetaData> alertasCoresValor;
    private List<AlertaCorMetaData> alertasCoresOutroCampo;
    private Map<String, List<AlertaCorMetaData>> alertasCoresValorFuncaoCampoRelativo;
    private Map<String, List<AlertaCorMetaData>> alertasCoresOutroCampoFuncaoCampoRelativo;
    private String agregacaoAplicarOrdem;
    public static final int LINHA = 1;
    public static final int COLUNA = 2;
    public static final String DIMENSAO = "D";
    public static final String METRICA = "V";
    public static final String TIPO_TEXTO = "S";
    public static final String TIPO_DATA = "D";
    public static final String TIPO_HORA = "H";
    public static final String TIPO_INTEIRO = "I";
    public static final String TIPO_DECIMAL = "N";
    public static final String TIPO_AV_NAO_APLICAR = "N";
    public static final String TIPO_AV_GERAL = "T";
    public static final String TIPO_AV_PARCIAL_PROX_NIVEL = "H";
    public static final String TIPO_AV_PARCIAL_PROX_NIVEL_TOTALIZADO = "P";
    public static final String TIPO_AH_NAO_APLICAR = "N";
    public static final String TIPO_AH_FIXA = "F";
    public static final String TIPO_AH_DINAMICA = "D";
    public static final String NAO_TOTALIZAR = "N";
    public static final String TOTALIZAR_APLICAR_SOMA = "S";
    public static final String TOTALIZAR_APLICAR_EXPRESSAO = "E";
    public static final String AGREGACAO_APLICAR_ANTES = "A";
    public static final String AGREGACAO_APLICAR_DEPOIS = "D";
    public static final String METRICA_ADICIONADA = "S";
    public static final String METRICA_NAO_ADICIONADA = "N";
    public static final String METRICA_VISUALIZACAO_RESTRITA = "T";

    public CampoMetaData() {
        this.mascarasCampo = new ArrayList<>();
        this.mascarasLinkHTML = new ArrayList<>();
        this.alertasCoresValorFuncaoCampoRelativo = new HashMap<>();
        this.alertasCoresOutroCampoFuncaoCampoRelativo = new HashMap<>();
        this.alertasCoresValor = new ArrayList<>();
        this.alertasCoresOutroCampo = new ArrayList<>();
        this.ordem = 0;
        this.ordemAcumulado = 0;
        this.numPosDecimais = 0;
        this.sentidoOrdem = "ASC";
        this.sentidoOrdemAcumulado = "ASC";
        this.analiseVerticalTipo = TIPO_AV_NAO_APLICAR;
        this.analiseHorizontalTipo = TIPO_AH_NAO_APLICAR;
        this.agregacaoAplicarOrdem = AGREGACAO_APLICAR_ANTES;
    }

    public boolean temParticipacaoAcumulada() {
        return participacaoAcumulada;
    }

    public boolean temValorAcumulado() {
        return valorAcumulado;
    }

    public boolean temParticipacaoHorizontal() {
        return participacaoHorizontal;
    }


    public boolean temParticipacaoAcumuladaHorizontal() {
        return participacaoAcumuladaHorizontal;
    }


    public void addMascara(MascaraColunaMetaData mascara) {
        this.mascarasCampo.add(mascara);
    }


    public void addMascaraLinkHTML(MascaraLinkHTMLMetaData mascara) {
        this.mascarasLinkHTML.add(mascara);
    }




    public boolean temAnaliseVertical() {
        return !TIPO_AV_NAO_APLICAR.equals(this.analiseVerticalTipo);
    }

    public void addAlertaCor(AlertaCorMetaData alertaCor, String tipoAlerta) {
        List<AlertaCorMetaData> alertasCores = null;
        Map<String, List<AlertaCorMetaData>> alertasCoresFuncaoCampoRelativo;
        if (AlertaCorMetaData.TIPO_ALERTA_VALOR.equals(tipoAlerta)) {
            alertasCores = this.alertasCoresValor;
            alertasCoresFuncaoCampoRelativo = this.alertasCoresValorFuncaoCampoRelativo;
        } else {
            alertasCores = this.alertasCoresOutroCampo;
            alertasCoresFuncaoCampoRelativo = this.alertasCoresOutroCampoFuncaoCampoRelativo;
        }
        if (!alertaCor.isFuncaoCampoRelativo()) {
            alertasCores.add(alertaCor);
        } else {
            String funcao = alertaCor.getFuncao();
            List<AlertaCorMetaData> alertasFuncao = alertasCoresFuncaoCampoRelativo.get(funcao);
            if (alertasFuncao == null) {
                alertasFuncao = new ArrayList<>();
            }
            alertaCor.setFuncao(AlertaCorMetaData.SEM_FUNCAO);
            alertasFuncao.add(alertaCor);
            alertasCoresFuncaoCampoRelativo.put(funcao, alertasFuncao);
        }
    }

    public List<AlertaCorMetaData> getAlertasValorFuncaoDeCampoRelativo(String funcaoCampoRelativo) {
        return this.alertasCoresValorFuncaoCampoRelativo.get(funcaoCampoRelativo);
    }

    public List<AlertaCorMetaData> getAlertasOutroCampoFuncaoDeCampoRelativo(String funcaoCampoRelativo) {
        return this.alertasCoresOutroCampoFuncaoCampoRelativo.get(funcaoCampoRelativo);
    }

    @Override
    public String toString() {
        return this.tituloCampo;
    }


    public boolean temAnaliseHorizontal() {
        return !TIPO_AH_NAO_APLICAR.equals(this.analiseHorizontalTipo);
    }

    public MascaraLinkHTMLMetaData getMascaraLinkHTMLByID(String idMascara) {
        for (MascaraLinkHTMLMetaData mascara : this.mascarasLinkHTML) {
            if (idMascara.equals(mascara.getIdMascara())) {
                return mascara;
            }
        }
        return null;
    }
}
