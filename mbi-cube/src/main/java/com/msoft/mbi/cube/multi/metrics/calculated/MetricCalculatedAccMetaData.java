package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.calculation.Calculation;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLColumnText;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascLinkHTMLTextRenderer;

public abstract class MetricCalculatedAccMetaData extends MetricCalculatedMetaData implements MetricCalculatedFunctionMetaData {

    public static final String AV_COLUMN_VARIABLE = "colunaAV";
    public static final String PREVIOUS_VALUE_COLUMN_VARIABLE = "valorAnterior";
    private final String referenceColumnTitle;
    private final AnalysisParticipationType analysisParticipationType;

    public MetricCalculatedAccMetaData(MetricMetaData referenceColumn, AnalysisParticipationType analysisParticipationType,
                                       List<ColorAlertMetadata> alertasCores) {
        super(referenceColumn.getTitle() + " Acum.");
        this.setTotalPartialColumns(false);
        this.setTotalLines(referenceColumn.isTotalLines());
        this.setDecimalPlaces(2);
        this.setCellProperty(referenceColumn.getCellProperty());
        this.referenceColumnTitle = referenceColumn.getTitle();
        this.expression = "[" + AV_COLUMN_VARIABLE + "]+[" + PREVIOUS_VALUE_COLUMN_VARIABLE + "]";
        this.analysisParticipationType = analysisParticipationType;
        if (alertasCores != null) {
            factoryColorsAlert(this, alertasCores);
        }

        LinkHTMLColumnText htmlColumnText = new LinkHTMLColumnText("", referenceColumn.getCellProperty().getWidth());
        MascLinkHTMLTextRenderer mascLinkHTMLTextRenderer = new MascLinkHTMLTextRenderer(htmlColumnText);
        this.setHTMLEffectRenderer(mascLinkHTMLTextRenderer);
    }

    @Override
    public String getReferenceFieldTitle() {
        return this.referenceColumnTitle;
    }

    @Override
    public AnalysisParticipationType getParticipationAnalysisType() {
        return this.analysisParticipationType;
    }

    @Override
    public MetricCalculatedAccumulated createMetrica() {
        MetricCalculatedAccumulated calculatedAccumulated = new MetricCalculatedAccumulated();
        calculatedAccumulated.setMetaData(this);
        calculatedAccumulated.setAggregator(this.aggregationType);
        return calculatedAccumulated;
    }

    @Override
    public Calculation createCalculo() {
        Calculation calculation = super.createCalculo();
        calculation.putVariable(AV_COLUMN_VARIABLE, this.referenceColumnTitle);
        return calculation;
    }
}
