package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.EvolutionAnalysisType;
import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationType;
import com.msoft.mbi.cube.multi.analytics.AnalysisParticipationTypeGeneral;
import com.msoft.mbi.cube.multi.calculation.Calculation;
import com.msoft.mbi.cube.multi.column.MaskColumnMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metadata.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metadata.MetaDataField;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLColumnText;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascLinkHTMLTextRenderer;
import lombok.Getter;

public class MetricCalculatedAHEvolutionMetaData extends MetricCalculatedMetaData implements MetricCalculatedFunctionMetaData {

    public static final String AH_COLUMN_VARIABLE = "colunaAH";
    public static final String PREVIOUS_VALUE_COLUMN_VARIABLE = "valorAnterior";
    private final String referenceColumnTitle;
    private final AnalysisParticipationType comparisonParticipationType;
    @Getter
    private final EvolutionAnalysisType evolutionAnalysisType;
    public static final String AH = "analiseHorizontal";

    public MetricCalculatedAHEvolutionMetaData(MetricMetaData referenceColumn, List<ColorAlertMetadata> colorAlerts, EvolutionAnalysisType evolutionAnalysisType) {
        super("AH% " + referenceColumn.getTitle());
        this.setTotalPartialLines(referenceColumn.isTotalPartialLines());
        this.setTotalPartialColumns(referenceColumn.isTotalPartialColumns());
        this.setTotalLines(referenceColumn.isTotalLines());
        this.setTotalLinesType(MetaDataField.TOTAL_APPLY_EXPRESSION);
        this.setDecimalPlaces(2);
        this.setCellProperty(referenceColumn.getCellProperty());
        MaskColumnMetaData mascaraAH = new MaskColumnMetaData("%", MaskColumnMetaData.TYPE_AFTER);
        this.addDecorator(mascaraAH);
        this.referenceColumnTitle = referenceColumn.getTitle();

        this.expression = "(([" + AH_COLUMN_VARIABLE + "]-[" + PREVIOUS_VALUE_COLUMN_VARIABLE + "])/" + "[" + PREVIOUS_VALUE_COLUMN_VARIABLE + "])*100";
        this.comparisonParticipationType = AnalysisParticipationTypeGeneral.getInstance();
        this.evolutionAnalysisType = evolutionAnalysisType;
        if (colorAlerts != null) {
            factoryColorsAlert(this, colorAlerts);
        }

        LinkHTMLColumnText htmlColumnText = new LinkHTMLColumnText("", referenceColumn.getCellProperty().getWidth());
        MascLinkHTMLTextRenderer mascLinkHTMLTextRenderer = new MascLinkHTMLTextRenderer(htmlColumnText);
        this.setHTMLEffectRenderer(mascLinkHTMLTextRenderer);
    }

    @Override
    public MetricCalculatedEvolutionAH createMetrica() {
        MetricCalculatedEvolutionAH calculatedEvolutionAH = new MetricCalculatedEvolutionAH();
        calculatedEvolutionAH.setMetaData(this);
        calculatedEvolutionAH.setAggregator(this.aggregationType);
        return calculatedEvolutionAH;
    }

    @Override
    public String getFieldFunction() {
        return MetricCalculatedAHEvolutionMetaData.AH;
    }

    @Override
    public String getReferenceFieldTitle() {
        return this.referenceColumnTitle;
    }

    @Override
    public AnalysisParticipationType getParticipationAnalysisType() {
        return this.comparisonParticipationType;
    }

    @Override
    public Dimension DimensionReferenceAxis(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

    @Override
    public Dimension getDimensionOther(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

    @Override
    public Calculation createCalculation() {
        Calculation calculation = super.createCalculation();
        calculation.putVariable(AH_COLUMN_VARIABLE, this.referenceColumnTitle);
        return calculation;
    }

}
