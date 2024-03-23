package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLColumnText;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascLinkHTMLTextRenderer;

public abstract class MetricCalculatedAcumuladoMetaData extends MetricCalculatedMetaData implements MetricCalculatedFunctionMetaData {

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

        LinkHTMLColumnText linkHTMLTextoColuna = new LinkHTMLColumnText("", colunaReferencia.getCellProperty().getWidth());
        MascLinkHTMLTextRenderer mascLinkHTMLTextRenderer = new MascLinkHTMLTextRenderer(linkHTMLTextoColuna);
        this.setHTMLEffectRenderer(mascLinkHTMLTextRenderer);
    }

    @Override
    public String getReferenceFieldTitle() {
        return this.tituloColunaReferencia;
    }

    @Override
    public AnaliseParticipacaoTipo getParticipationAnalysisType() {
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
