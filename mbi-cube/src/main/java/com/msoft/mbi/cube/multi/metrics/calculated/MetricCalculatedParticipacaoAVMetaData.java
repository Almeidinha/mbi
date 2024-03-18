package com.msoft.mbi.cube.multi.metrics.calculated;

import java.util.List;

import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.analytics.AnaliseParticipacaoTipo;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;

public class MetricCalculatedParticipacaoAVMetaData extends MetricCalculatedParticipacaoMetaData {

    public static final String AV = "analiseVertical";

    public MetricCalculatedParticipacaoAVMetaData(MetricMetaData colunaReferencia, AnaliseParticipacaoTipo analiseVerticalTipo, List<AlertaCorMetaData> alertasCores) {
        super("AV % Participação", colunaReferencia, analiseVerticalTipo, alertasCores);
    }

    @Override
    public String getFuncaoCampo() {
        return MetricCalculatedParticipacaoAVMetaData.AV;
    }

    @Override
    public Dimension getDimensaoEixoReferencia(MetricLine metricLine) {
        return metricLine.getDimensionLine();
    }

    @Override
    public Dimension getDimensaoOutra(MetricLine metricLine) {
        return metricLine.getDimensionColumn();
    }

}