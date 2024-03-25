package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.calculation.Calculation;
import com.msoft.mbi.cube.multi.column.MaskColumnMetaData;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLColumnText;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascLinkHTMLTextRenderer;

public abstract class MetricCalculatedParticipacaoMetaData extends MetricCalculatedMetaData implements MetricCalculatedFunctionMetaData {

    public static final String COLUNA_AV_VARIABLE = "colunaAV";
    public static final String VALOR_NIVEL_ACIMA_VARIABLE = "valorAcima";
    private final String tituloColunaReferencia;
    private final AnalysisParticipationType analiseVertical;

    public MetricCalculatedParticipacaoMetaData(String prefixoTitulo, MetricMetaData colunaParticipacao, AnalysisParticipationType analiseVerticalTipo,
                                                List<ColorAlertMetadata> alertasCores) {
        super(prefixoTitulo + " " + colunaParticipacao.getTitle());
        this.setUsePercent(true);
        this.setTotalPartialLines(colunaParticipacao.isTotalPartialLines());
        this.setTotalPartialColumns(colunaParticipacao.isTotalPartialColumns());
        this.setTotalLinesType(MetaDataField.TOTAL_APPLY_EXPRESSION);
        this.setDecimalPlaces(2);
        this.setCellProperty(colunaParticipacao.getCellProperty());
        MaskColumnMetaData mascaraAV = new MaskColumnMetaData("%", MaskColumnMetaData.TYPE_AFTER);
        this.addDecorator(mascaraAV);
        this.tituloColunaReferencia = colunaParticipacao.getTitle();
        this.expression = "([" + COLUNA_AV_VARIABLE + "]/[" + VALOR_NIVEL_ACIMA_VARIABLE + "])*100";
        this.analiseVertical = analiseVerticalTipo;
        if (alertasCores != null) {
            factoryColorsAlert(this, alertasCores);
        }

        LinkHTMLColumnText linkHTMLTextoColuna = new LinkHTMLColumnText("", colunaParticipacao.getCellProperty().getWidth());
        MascLinkHTMLTextRenderer mascLinkHTMLTextRenderer = new MascLinkHTMLTextRenderer(linkHTMLTextoColuna);
        this.setHTMLEffectRenderer(mascLinkHTMLTextRenderer);
    }

    @Override
    public MetricCalculatedParticipation createMetrica() {
        MetricCalculatedParticipation metricaCalculadaParticipacao = new MetricCalculatedParticipation();
        metricaCalculadaParticipacao.setMetaData(this);
        metricaCalculadaParticipacao.setAggregator(this.aggregationType);
        return metricaCalculadaParticipacao;
    }

    public AnalysisParticipationType getParticipationAnalysisType() {
        return analiseVertical;
    }

    @Override
    public String getReferenceFieldTitle() {
        return this.tituloColunaReferencia;
    }

    @Override
    public Calculation createCalculo() {
        Calculation calculation = super.createCalculo();
        calculation.putVariable(COLUNA_AV_VARIABLE, this.getReferenceFieldTitle());
        return calculation;
    }

}
