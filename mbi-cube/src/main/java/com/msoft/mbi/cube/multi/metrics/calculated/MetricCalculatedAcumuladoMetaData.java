package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLTextoColuna;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLTextoRenderer;

public abstract class MetricCalculatedAcumuladoMetaData extends MetricCalculatedMetaData implements MetricaCalculadaFuncaoMetaData {

    public static final String COLUNA_AV_VARIABLE = "colunaAV";
    public static final String COLUNA_VALOR_ANTERIOR_VARIABLE = "valorAnterior";
    private final String tituloColunaReferencia;
    private final AnaliseParticipacaoTipo analiseParticipacaoTipo;

    public MetricCalculatedAcumuladoMetaData(MetricMetaData colunaReferencia, AnaliseParticipacaoTipo analiseParticipacaoTipo,
                                             List<ColorAlertMetadata> alertasCores) {
        super(colunaReferencia.getTitle() + " Acum.");
        this.setTotalPartialColumns(false);
        this.setTotalLines(colunaReferencia.isTotalLines());
        this.setDecimalPlaces(2);
        this.setCellProperty(colunaReferencia.getCellProperty());
        this.tituloColunaReferencia = colunaReferencia.getTitle();
        this.expression = "[" + COLUNA_AV_VARIABLE + "]+[" + COLUNA_VALOR_ANTERIOR_VARIABLE + "]";
        this.analiseParticipacaoTipo = analiseParticipacaoTipo;
        if (alertasCores != null) {
            factoryColorsAlert(this, alertasCores);
        }

        LinkHTMLTextoColuna linkHTMLTextoColuna = new LinkHTMLTextoColuna("", colunaReferencia.getCellProperty().getWidth());
        MascaraLinkHTMLTextoRenderer mascaraLinkHTMLTextoRenderer = new MascaraLinkHTMLTextoRenderer(linkHTMLTextoColuna);
        this.setHTMLEffectRenderer(mascaraLinkHTMLTextoRenderer);
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
    public MetricCalculatedAcumulado createMetrica() {
        MetricCalculatedAcumulado metricaCalculadaValorAcumulado = new MetricCalculatedAcumulado();
        metricaCalculadaValorAcumulado.setMetaData(this);
        metricaCalculadaValorAcumulado.setAggregator(this.aggregationType);
        return metricaCalculadaValorAcumulado;
    }

    @Override
    public Calculo createCalculo() {
        Calculo calculo = super.createCalculo();
        calculo.putVariable(COLUNA_AV_VARIABLE, this.tituloColunaReferencia);
        return calculo;
    }
}
