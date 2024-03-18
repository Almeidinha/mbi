package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLTextoColuna;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLTextoRenderer;

public abstract class MetricaCalculadaAcumuladoMetaData extends MetricaCalculadaMetaData implements MetricaCalculadaFuncaoMetaData {

    public static final String COLUNA_AV_VARIABLE = "colunaAV";
    public static final String COLUNA_VALOR_ANTERIOR_VARIABLE = "valorAnterior";
    private final String tituloColunaReferencia;
    private final AnaliseParticipacaoTipo analiseParticipacaoTipo;

    public MetricaCalculadaAcumuladoMetaData(MetricaMetaData colunaReferencia, AnaliseParticipacaoTipo analiseParticipacaoTipo,
                                             List<AlertaCorMetaData> alertasCores) {
        super(colunaReferencia.getTitulo() + " Acum.");
        this.setTotalizarParcialColunas(false);
        this.setTotalizarLinhas(colunaReferencia.isTotalizarLinhas());
        this.setNCasasDecimais(2);
        this.setCellProperty(colunaReferencia.getCellProperty());
        this.tituloColunaReferencia = colunaReferencia.getTitulo();
        this.expressao = "[" + COLUNA_AV_VARIABLE + "]+[" + COLUNA_VALOR_ANTERIOR_VARIABLE + "]";
        this.analiseParticipacaoTipo = analiseParticipacaoTipo;
        if (alertasCores != null) {
            factoryAlertasCores(this, alertasCores);
        }

        LinkHTMLTextoColuna linkHTMLTextoColuna = new LinkHTMLTextoColuna("", colunaReferencia.getCellProperty().getWidth());
        MascaraLinkHTMLTextoRenderer mascaraLinkHTMLTextoRenderer = new MascaraLinkHTMLTextoRenderer(linkHTMLTextoColuna);
        this.setEfeitosHTMLDecorator(mascaraLinkHTMLTextoRenderer);
    }

    @Override
    public String getTituloCampoReferencia() {
        return this.tituloColunaReferencia;
    }

    @Override
    public AnaliseParticipacaoTipo getAnaliseParticipacaoTipo() {
        return this.analiseParticipacaoTipo;
    }

    @Override
    public MetricaCalculadaAcumulado createMetrica() {
        MetricaCalculadaAcumulado metricaCalculadaValorAcumulado = new MetricaCalculadaAcumulado();
        metricaCalculadaValorAcumulado.setMetaData(this);
        metricaCalculadaValorAcumulado.setAgregador(this.agregacaoTipo);
        return metricaCalculadaValorAcumulado;
    }

    @Override
    public Calculo createCalculo() {
        Calculo calculo = super.createCalculo();
        calculo.putVariable(COLUNA_AV_VARIABLE, this.tituloColunaReferencia);
        return calculo;
    }
}
