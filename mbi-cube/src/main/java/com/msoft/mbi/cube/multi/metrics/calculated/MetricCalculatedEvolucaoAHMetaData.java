package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseEvolucaoTipo;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoGeral;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.column.MascaraColunaMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLColumnText;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascLinkHTMLTextRenderer;
import lombok.Getter;

public class MetricCalculatedEvolucaoAHMetaData extends MetricCalculatedMetaData implements MetricCalculatedFunctionMetaData {

    public static final String COLUNA_AH_VARIABLE = "colunaAH";
    public static final String COLUNA_VALOR_ANTERIOR_VARIABLE = "valorAnterior";
    private final String tituloColunaReferencia;
    private final AnaliseParticipacaoTipo tipoComparacaoParticipacao;
    @Getter
    private final AnaliseEvolucaoTipo analiseEvolucaoTipo;
    public static final String AH = "analiseHorizontal";

    public MetricCalculatedEvolucaoAHMetaData(MetricMetaData colunaReferencia, List<ColorAlertMetadata> alertasCores, AnaliseEvolucaoTipo tipoAnaliseEvolucao) {
        super("AH% " + colunaReferencia.getTitle());
        this.setTotalPartialLines(colunaReferencia.isTotalPartialLines());
        this.setTotalPartialColumns(colunaReferencia.isTotalPartialColumns());
        this.setTotalLines(colunaReferencia.isTotalLines());
        this.setTotalLinesType(MetaDataField.TOTAL_APPLY_EXPRESSION);
        this.setDecimalPlaces(2);
        this.setCellProperty(colunaReferencia.getCellProperty());
        MascaraColunaMetaData mascaraAH = new MascaraColunaMetaData("%", MascaraColunaMetaData.TIPO_DEPOIS);
        this.addDecorator(mascaraAH);
        this.tituloColunaReferencia = colunaReferencia.getTitle();

        this.expression = "(([" + COLUNA_AH_VARIABLE + "]-[" + COLUNA_VALOR_ANTERIOR_VARIABLE + "])/" + "[" + COLUNA_VALOR_ANTERIOR_VARIABLE + "])*100";
        this.tipoComparacaoParticipacao = AnaliseParticipacaoTipoGeral.getInstance();
        this.analiseEvolucaoTipo = tipoAnaliseEvolucao;
        if (alertasCores != null) {
            factoryColorsAlert(this, alertasCores);
        }

        LinkHTMLColumnText linkHTMLTextoColuna = new LinkHTMLColumnText("", colunaReferencia.getCellProperty().getWidth());
        MascLinkHTMLTextRenderer mascLinkHTMLTextRenderer = new MascLinkHTMLTextRenderer(linkHTMLTextoColuna);
        this.setHTMLEffectRenderer(mascLinkHTMLTextRenderer);
    }

    @Override
    public MetricCalculatedEvolucaoAH createMetrica() {
        MetricCalculatedEvolucaoAH metricaCalculadaAH = new MetricCalculatedEvolucaoAH();
        metricaCalculadaAH.setMetaData(this);
        metricaCalculadaAH.setAggregator(this.aggregationType);
        return metricaCalculadaAH;
    }

    @Override
    public String getFieldFunction() {
        return MetricCalculatedEvolucaoAHMetaData.AH;
    }

    @Override
    public String getReferenceFieldTitle() {
        return this.tituloColunaReferencia;
    }

    @Override
    public AnaliseParticipacaoTipo getParticipationAnalysisType() {
        return this.tipoComparacaoParticipacao;
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
    public Calculo createCalculo() {
        Calculo calculo = super.createCalculo();
        calculo.putVariable(COLUNA_AH_VARIABLE, this.tituloColunaReferencia);
        return calculo;
    }

}
