package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.column.MascaraColunaMetaData;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLTextoColuna;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLTextoRenderer;

public abstract class MetricaCalculadaParticipacaoMetaData extends MetricaCalculadaMetaData implements MetricaCalculadaFuncaoMetaData {

    public static final String COLUNA_AV_VARIABLE = "colunaAV";
    public static final String VALOR_NIVEL_ACIMA_VARIABLE = "valorAcima";
    private final String tituloColunaReferencia;
    private final AnaliseParticipacaoTipo analiseVertical;

    public MetricaCalculadaParticipacaoMetaData(String prefixoTitulo, MetricaMetaData colunaParticipacao, AnaliseParticipacaoTipo analiseVerticalTipo,
                                                List<AlertaCorMetaData> alertasCores) {
        super(prefixoTitulo + " " + colunaParticipacao.getTitulo());
        this.setUtilizaPercentual(true);
        this.setTotalizarParcialLinhas(colunaParticipacao.isTotalizarParcialLinhas());
        this.setTotalizarParcialColunas(colunaParticipacao.isTotalizarParcialColunas());
        this.setTipoTotalizacaoLinhas(CampoMetaData.TOTALIZAR_APLICAR_EXPRESSAO);
        this.setNCasasDecimais(2);
        this.setCellProperty(colunaParticipacao.getCellProperty());
        MascaraColunaMetaData mascaraAV = new MascaraColunaMetaData("%", MascaraColunaMetaData.TIPO_DEPOIS);
        this.addDecorator(mascaraAV);
        this.tituloColunaReferencia = colunaParticipacao.getTitulo();
        this.expressao = "([" + COLUNA_AV_VARIABLE + "]/[" + VALOR_NIVEL_ACIMA_VARIABLE + "])*100";
        this.analiseVertical = analiseVerticalTipo;
        if (alertasCores != null) {
            factoryAlertasCores(this, alertasCores);
        }

        LinkHTMLTextoColuna linkHTMLTextoColuna = new LinkHTMLTextoColuna("", colunaParticipacao.getCellProperty().getWidth());
        MascaraLinkHTMLTextoRenderer mascaraLinkHTMLTextoRenderer = new MascaraLinkHTMLTextoRenderer(linkHTMLTextoColuna);
        this.setEfeitosHTMLDecorator(mascaraLinkHTMLTextoRenderer);
    }

    @Override
    public MetricaCalculadaParticipacao createMetrica() {
        MetricaCalculadaParticipacao metricaCalculadaParticipacao = new MetricaCalculadaParticipacao();
        metricaCalculadaParticipacao.setMetaData(this);
        metricaCalculadaParticipacao.setAgregador(this.agregacaoTipo);
        return metricaCalculadaParticipacao;
    }

    public AnaliseParticipacaoTipo getAnaliseParticipacaoTipo() {
        return analiseVertical;
    }

    @Override
    public String getTituloCampoReferencia() {
        return this.tituloColunaReferencia;
    }

    @Override
    public Calculo createCalculo() {
        Calculo calculo = super.createCalculo();
        calculo.putVariable(COLUNA_AV_VARIABLE, this.getTituloCampoReferencia());
        return calculo;
    }

}
