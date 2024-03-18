package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.analytics.AnaliseEvolucaoTipo;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoGeral;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.column.MascaraColunaMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLTextoColuna;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLTextoRenderer;

public class MetricaCalculadaEvolucaoAHMetaData extends MetricaCalculadaMetaData implements MetricaCalculadaFuncaoMetaData {

    private static final long serialVersionUID = 1590485166352081678L;
    public static final String COLUNA_AH_VARIABLE = "colunaAH";
    public static final String COLUNA_VALOR_ANTERIOR_VARIABLE = "valorAnterior";
    private String tituloColunaReferencia;
    private AnaliseParticipacaoTipo tipoComparacaoParticipacao;
    private AnaliseEvolucaoTipo analiseEvolucaoTipo;
    public static final String AH = "analiseHorizontal";

    public MetricaCalculadaEvolucaoAHMetaData(MetricaMetaData colunaReferencia, List<AlertaCorMetaData> alertasCores, AnaliseEvolucaoTipo tipoAnaliseEvolucao) {
        super("AH% " + colunaReferencia.getTitulo());
        this.setTotalizarParcialLinhas(colunaReferencia.isTotalizarParcialLinhas());
        this.setTotalizarParcialColunas(colunaReferencia.isTotalizarParcialColunas());
        this.setTotalizarLinhas(colunaReferencia.isTotalizarLinhas());
        this.setTipoTotalizacaoLinhas(CampoMetaData.TOTALIZAR_APLICAR_EXPRESSAO);
        this.setNCasasDecimais(2);
        this.setCellProperty(colunaReferencia.getCellProperty());
        MascaraColunaMetaData mascaraAH = new MascaraColunaMetaData("%", MascaraColunaMetaData.TIPO_DEPOIS);
        this.addDecorator(mascaraAH);
        this.tituloColunaReferencia = colunaReferencia.getTitulo();

        this.expressao = "(([" + COLUNA_AH_VARIABLE + "]-[" + COLUNA_VALOR_ANTERIOR_VARIABLE + "])/" + "[" + COLUNA_VALOR_ANTERIOR_VARIABLE + "])*100";
        this.tipoComparacaoParticipacao = AnaliseParticipacaoTipoGeral.getInstance();
        this.analiseEvolucaoTipo = tipoAnaliseEvolucao;
        if (alertasCores != null) {
            factoryAlertasCores(this, alertasCores);
        }

        LinkHTMLTextoColuna linkHTMLTextoColuna = new LinkHTMLTextoColuna("", colunaReferencia.getCellProperty().getWidth());
        MascaraLinkHTMLTextoRenderer mascaraLinkHTMLTextoRenderer = new MascaraLinkHTMLTextoRenderer(linkHTMLTextoColuna);
        this.setEfeitosHTMLDecorator(mascaraLinkHTMLTextoRenderer);
    }

    @Override
    public MetricaCalculadaEvolucaoAH createMetrica() {
        MetricaCalculadaEvolucaoAH metricaCalculadaAH = new MetricaCalculadaEvolucaoAH();
        metricaCalculadaAH.setMetaData(this);
        metricaCalculadaAH.setAgregador(this.agregacaoTipo);
        return metricaCalculadaAH;
    }

    public AnaliseEvolucaoTipo getAnaliseEvolucaoTipo() {
        return analiseEvolucaoTipo;
    }

    @Override
    public String getFuncaoCampo() {
        return MetricaCalculadaEvolucaoAHMetaData.AH;
    }

    @Override
    public String getTituloCampoReferencia() {
        return this.tituloColunaReferencia;
    }

    @Override
    public AnaliseParticipacaoTipo getAnaliseParticipacaoTipo() {
        return this.tipoComparacaoParticipacao;
    }

    @Override
    public Dimension getDimensaoEixoReferencia(LinhaMetrica linhaMetrica) {
        return linhaMetrica.getDimensionLinha();
    }

    @Override
    public Dimension getDimensaoOutra(LinhaMetrica linhaMetrica) {
        return linhaMetrica.getDimensionColuna();
    }

    @Override
    public Calculo createCalculo() {
        Calculo calculo = super.createCalculo();
        calculo.putVariable(COLUNA_AH_VARIABLE, this.tituloColunaReferencia);
        return calculo;
    }

}
