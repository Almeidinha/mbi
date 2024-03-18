package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseEvolucaoTipo;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipoGeral;
import com.msoft.mbi.cube.multi.calculation.Calculo;
import com.msoft.mbi.cube.multi.column.MascaraColunaMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLTextoColuna;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLTextoRenderer;
import lombok.Getter;

public class MetricCalculatedEvolucaoAHMetaData extends MetricCalculatedMetaData implements MetricaCalculadaFuncaoMetaData {

    public static final String COLUNA_AH_VARIABLE = "colunaAH";
    public static final String COLUNA_VALOR_ANTERIOR_VARIABLE = "valorAnterior";
    private final String tituloColunaReferencia;
    private final AnaliseParticipacaoTipo tipoComparacaoParticipacao;
    @Getter
    private final AnaliseEvolucaoTipo analiseEvolucaoTipo;
    public static final String AH = "analiseHorizontal";

    public MetricCalculatedEvolucaoAHMetaData(MetricMetaData colunaReferencia, List<AlertaCorMetaData> alertasCores, AnaliseEvolucaoTipo tipoAnaliseEvolucao) {
        super("AH% " + colunaReferencia.getTitle());
        this.setTotalPartialLines(colunaReferencia.isTotalPartialLines());
        this.setTotalPartialColumns(colunaReferencia.isTotalPartialColumns());
        this.setTotalLines(colunaReferencia.isTotalLines());
        this.setTotalLinesType(CampoMetaData.TOTALIZAR_APLICAR_EXPRESSAO);
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

        LinkHTMLTextoColuna linkHTMLTextoColuna = new LinkHTMLTextoColuna("", colunaReferencia.getCellProperty().getWidth());
        MascaraLinkHTMLTextoRenderer mascaraLinkHTMLTextoRenderer = new MascaraLinkHTMLTextoRenderer(linkHTMLTextoColuna);
        this.setHTMLEffectRenderer(mascaraLinkHTMLTextoRenderer);
    }

    @Override
    public MetricCalculatedEvolucaoAH createMetrica() {
        MetricCalculatedEvolucaoAH metricaCalculadaAH = new MetricCalculatedEvolucaoAH();
        metricaCalculadaAH.setMetaData(this);
        metricaCalculadaAH.setAggregator(this.aggregationType);
        return metricaCalculadaAH;
    }

    @Override
    public String getFuncaoCampo() {
        return MetricCalculatedEvolucaoAHMetaData.AH;
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
    public Dimension getDimensaoEixoReferencia(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

    @Override
    public Dimension getDimensaoOutra(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

    @Override
    public Calculo createCalculo() {
        Calculo calculo = super.createCalculo();
        calculo.putVariable(COLUNA_AH_VARIABLE, this.tituloColunaReferencia);
        return calculo;
    }

}
