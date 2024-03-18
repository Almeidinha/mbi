package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class MetricaCalculadaAcumuladoParticipacaoAVMetaData extends MetricaCalculadaAcumuladoParticipacaoMetaData {

    public static final String PARTICIPACAO_ACUMULADA_AV = "participacaoAcumulada";

    public MetricaCalculadaAcumuladoParticipacaoAVMetaData(MetricaMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo, List<AlertaCorMetaData> alertasCores) {
        super(colunaReferencia, analiseVerticalTipo, alertasCores);
        this.setTotalizarParcialLinhas(false);
        this.setTotalizarLinhas(false);
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
    public String getFuncaoCampo() {
        return MetricaCalculadaAcumuladoParticipacaoAVMetaData.PARTICIPACAO_ACUMULADA_AV;
    }
}
